/*
 * compute_vocab.cpp
 *
 *  Created on: 15-Mar-2013
 *      Author: rohit
 */

#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/calib3d/calib3d.hpp>
#include <dirent.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <vector>
#include <string>
#include <iostream>

#define NUM_CLUSTER 200

using namespace std;
using namespace cv;

int main() {
	vector<KeyPoint> kp;
	Ptr<FeatureDetector> det = FeatureDetector::create("ORB");
    Ptr<DescriptorExtractor> ext = DescriptorExtractor::create("ORB");
//    SiftFeatureDetector det;
	DIR *d;
	string dir = "TRAIN/", filepath;
	d = opendir(dir.c_str());
	Mat desc;
	Mat img;
	Mat train_descs(1,ext->descriptorSize(),CV_32FC1);

	cout<< "====Building Vocab===="<<endl;
	cout<<"Extract Descriptors"<<endl;

	struct dirent *dirp;
	struct stat filestat;
	while(dirp = readdir(d)) {
		filepath = dir + dirp->d_name;
		if (stat( filepath.c_str(), &filestat )) 	continue;
		if (S_ISDIR( filestat.st_mode )) 			continue;

		img = imread(filepath);
		if(!img.data) {
			continue;
	    }
	    //FAST(img, kp, 60);
        det->detect(img, kp);
		ext->compute(img, kp, desc);
        Mat desc2;
        desc.convertTo(desc2, CV_32FC1);
		train_descs.push_back(desc2);
		cout<<".";
	}
	cout<<endl;

	cout<<"Total Descriptors: "<<train_descs.rows <<endl;
	FileStorage fs("training_descriptors.yml", FileStorage::WRITE);
	fs << "training_descriptors" << train_descs;
	fs.release();

	BOWKMeansTrainer bowtrainer(NUM_CLUSTER); // num of clusters
	bowtrainer.add(train_descs);
	cout<<"Cluster BOW features"<<endl;
	Mat vocab = bowtrainer.cluster();

	FileStorage fs1("vocab.yml", FileStorage::WRITE);
	fs1<<"vocabulary"<<vocab;
	fs1.release();
}


