#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/calib3d/calib3d.hpp"
#include <vector>
#include <android/log.h>
#include <sstream>

#define APPNAME "BookAlive"
using namespace cv;
using namespace std;

extern "C" {

Mat findHomography(Mat orig, Mat test) {
    vector<KeyPoint> kp_orig, kp_test;
    FAST(orig, kp_orig, 80);
    FAST(test, kp_test, 80);
    FREAK ext;
    Mat desc_orig, desc_test;
    ext.compute(orig, kp_orig, desc_orig);
    ext.compute(test, kp_test, desc_test);

    BFMatcher matcher(NORM_HAMMING);
    vector<DMatch> matches;
    matcher.match(desc_orig, desc_test, matches);

    double min_dist = 100, max_dist = 0;
    for(size_t i=0; i<desc_orig.rows; i++) {
        double dist = matches[i].distance;
        if(dist < min_dist) min_dist = dist;
        if(dist > max_dist) max_dist = dist;
    }
    double acceptable_dist = 3*min_dist;
    vector<DMatch> good_matches;
    for(size_t i=0; i<desc_orig.rows; i++) {
        if(matches[i].distance < acceptable_dist) {
            good_matches.push_back(matches[i]);
        }
    }
    vector<Point2f> orig_pts;
    vector<Point2f> test_pts;

    for( size_t i = 0; i < good_matches.size(); i++ ) {
        //-- Get the keypoints from the good matches
        orig_pts.push_back( kp_orig[ good_matches[i].queryIdx ].pt );
        test_pts.push_back( kp_test[ good_matches[i].trainIdx ].pt );
    }
    Mat H = findHomography( orig_pts, test_pts, CV_RANSAC );
    return H;
}

Mat getHomography(Mat orig, Mat test) {
    vector<KeyPoint> kp_orig, kp_test;
    FAST(orig, kp_orig, 10);
    FAST(test, kp_test, 10);
    __android_log_write(ANDROID_LOG_INFO, "vision.cpp", "Keypoints computed");

    // TODO remove this
    char temp[50];
    sprintf(temp, "%d, %d", kp_test.size(), kp_orig.size());
    __android_log_write(ANDROID_LOG_INFO, "vision.cpp-size", temp);

    FREAK ext;
    Mat desc_orig, desc_test;
    ext.compute(orig, kp_orig, desc_orig);
    ext.compute(test, kp_test, desc_test);

    BFMatcher matcher(NORM_HAMMING);
    vector<DMatch> matches;
    matcher.match(desc_orig, desc_test, matches);

    __android_log_write(ANDROID_LOG_INFO, "vision.cpp", "Matching done");

    double min_dist = 100, max_dist = 0;
    for(int i=0; i<desc_orig.rows; i++) {
    	DMatch d = matches[i];
        double dist = d.distance;
        if(dist < min_dist) min_dist = dist;
        if(dist > max_dist) max_dist = dist;
    }
    double acceptable_dist = 3*min_dist;
    vector<DMatch> good_matches;
    for(int i=0; i<desc_orig.rows; i++) {
    	DMatch d = matches[i];
        if(d.distance < acceptable_dist) {
            good_matches.push_back(d);
        }
    }
    vector<Point2f> orig_pts;
    vector<Point2f> test_pts;

    for( int i = 0; i < good_matches.size(); i++ ) {
        //-- Get the keypoints from the good matches
    	DMatch match = good_matches[i];
    	KeyPoint kp1 = kp_orig[ match.queryIdx ];
    	KeyPoint kp2 = kp_orig[ match.trainIdx ];
        orig_pts.push_back( kp1.pt );
        test_pts.push_back( kp2.pt );
    }
    Mat H = findHomography( orig_pts, test_pts, CV_RANSAC );
    __android_log_write(ANDROID_LOG_INFO, "vision.cpp", "Computed Homography");

    return H;
}

void setSize(Mat orig1, Mat test1, Mat& orig, Mat& test) {
	double facX = 0.75, facY = 0.75;
	resize(orig1, orig, Size(0,0), facX, facY);
	resize(test1, test, Size(0,0), facX, facY);
	cvtColor(orig, orig, CV_BGR2GRAY);
	cvtColor(test, test, CV_BGR2GRAY);
}

JNIEXPORT void JNICALL Java_com_rohit_bookalive_CapturedImage_computeHomography(JNIEnv*, jobject, jlong addrOrig, jlong addrImg, jlong addrH) {
	Mat& Orig1 = *(Mat*)addrOrig;
	Mat& Img1 = *(Mat*)addrImg;

	Mat Orig, Img;
	setSize(Orig1, Img1, Orig, Img);

	Mat& H = *(Mat*)addrH;
	H = findHomography(Orig, Img);
	char res[50];
	sprintf(res, "%lf %lf %lf", H.at<double>(0,0), H.at<double>(0,1), H.at<double>(0,2));
	__android_log_write(ANDROID_LOG_INFO, "vision.cpp-H", res);
}

JNIEXPORT void JNICALL Java_com_rohit_bookalive_Util_mapPoint(JNIEnv*, jobject, jlong addrH, jlong addrP) {
	Mat& Pnt = *(Mat*)addrP;
	Mat& H = *(Mat*)addrH;
	Point2f p, p2;
	p.x =Pnt.at<double>(0,0); p.y =Pnt.at<double>(0,1);
	vector<Point2f> pt, pt2; pt.push_back(p);
	perspectiveTransform(pt, pt2, H);
	p2 = pt2[0];
	Pnt.at<double>(0,0) = p2.x; Pnt.at<double>(0,1) = p2.y;
}

}
