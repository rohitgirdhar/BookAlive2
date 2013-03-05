#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/calib3d/calib3d.hpp"
#include <vector>
#include <android/log.h>

#define APPNAME "BookAlive"
using namespace cv;
using namespace std;

extern "C" {
Mat getHomography(Mat orig, Mat test) {
    vector<KeyPoint> kp_orig, kp_test;
    FAST(orig, kp_orig, 10);
    FAST(test, kp_test, 10);

    FREAK ext;
    Mat desc_orig, desc_test;
    ext.compute(orig, kp_orig, desc_orig);
    ext.compute(test, kp_test, desc_test);

    BFMatcher matcher(NORM_HAMMING);
    vector<DMatch> matches;
    matcher.match(desc_orig, desc_test, matches);

    double min_dist = 100, max_dist = 0;
    for(int i=0; i<desc_orig.rows; i++) {
    	DMatch d = matches[i];
        double dist = d.distance;
        if(dist < min_dist) min_dist = dist;
        if(dist > max_dist) max_dist = dist;
    }
    double acceptable_dist = 2*min_dist;
    vector<DMatch> good_matches;
    for(int i=0; i<desc_orig.rows; i++) {
    	DMatch d = matches[i];
        if(d.distance < acceptable_dist) {
            good_matches.push_back	(d);
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
    return H;
}

JNIEXPORT void JNICALL Java_com_rohit_bookalive_CapturedImage_computeHomography(JNIEnv*, jobject, jlong addrOrig, jlong addrImg, jlong addrH) {
	Mat& Orig = *(Mat*)addrOrig;
	Mat& Img = *(Mat*)addrImg;
	Mat& H = *(Mat*)addrH;
	H = getHomography(Orig, Img);
}

JNIEXPORT void JNICALL Java_com_rohit_bookalive_CapturedImage_mapPoints(JNIEnv*, jobject, jlong addrH, jlong addrP) {
	Mat& Pnt = *(Mat*)addrP;
	Mat& H = *(Mat*)addrH;
	Point2f p, p2;
	p.x =Pnt.at<double>(0,0); p.y =Pnt.at<double>(0,1);
	vector<Point2f> pt, pt2; pt.push_back(p);
	perspectiveTransform(pt, pt2, H);
	p2 = pt2[0];
	p.x = p2.x; p.y = p2.y;
}


}
