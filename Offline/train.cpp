/*
 * train_bow.cpp
 *
 *  Created on: 15-Mar-2013
 *      Author: rohit
 */

#include <iostream>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/calib3d/calib3d.hpp>
#include <opencv2/opencv.hpp>
#include <opencv2/nonfree/features2d.hpp>

#include <dirent.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <string>
#include <vector>
#include <cstdlib>
#include <cstring>
#include <sstream>
using namespace std;
using namespace cv;

int getClass(char fname[]) {
	// ends with .jpg
	string temp;
	for(int i=0; fname[i]!='\0'; i++) {
		if(fname[i] != '.') temp += fname;
	}
	return atoi(temp.c_str());
}

double getDist2(Mat a, Mat b) {
    Mat a1, b1;
    a.convertTo(a1, CV_32FC1);
    b.convertTo(b1, CV_32FC1);
    Mat temp = a1-b1;
    return norm(temp);
}

vector<int> computeVisualWords(Mat desc, Mat vocab) {
    vector<int> corr_vocab_word;
    for(size_t i=0; i<desc.rows; i++) {
        int minD = 99999999;
        int minI = 0;
        for(size_t j=0; j<vocab.rows; j++) {
            double d = getDist2(desc.row(i), vocab.row(j));
//            cout<<d<<endl;
            if(d < minD) {
                minD = d;
                minI = j;
            }
        }
        corr_vocab_word.push_back(minI);
    }
    return corr_vocab_word;
}

vector<int> computeHist(vector<int> visual_words, int nbins) {
    vector<int> hist;
    for(int i=0; i<nbins; i++) hist.push_back(0);
    for(size_t i=0; i<visual_words.size(); i++) {
        hist[visual_words[i]] ++;
    }
    return hist;
}

int main() {
	cout<<"Reading the vocab"<<endl;
	Mat vocab;
	FileStorage fs("vocab.yml", FileStorage::READ);
	fs["vocabulary"] >> vocab;
	fs.release();

	DIR *d;
	string filepath, dir = "TRAIN/";
	struct dirent *dirp;
	d = opendir(dir.c_str());
	struct stat filestat;

    ORB de = ORB(100);
    vector< vector<int> > invIndex;
    Mat invIndex_mat;
    vector<int> keys;

	while(dirp = readdir(d)) {
		filepath = dir + dirp->d_name;
		if (stat( filepath.c_str(), &filestat )) 	continue;
		if (S_ISDIR( filestat.st_mode )) 			continue;

		Mat img = imread(filepath);

		if(!img.data) {
			continue;
		}

		vector<KeyPoint> kp;
		Mat response_hist;
        Mat desc;
        de(img, Mat(), kp, desc);
        vector<int> visual_words = computeVisualWords(desc, vocab);
        vector<int> hist = computeHist(visual_words, vocab.rows);

		int cls = getClass(dirp->d_name);
        keys.push_back(cls);

        invIndex.push_back(hist);
        Mat temp = Mat(hist);
        temp = temp.t();
        invIndex_mat.push_back(temp);
	}


    FileStorage fw = FileStorage("hists.yml", FileStorage::WRITE);
    fw <<"hists"<<Mat(invIndex_mat);
    fw<<"keys"<<Mat(keys);
    fw.release();
    return 0;
}



