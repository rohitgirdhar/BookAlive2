#!/bin/bash

# Give the animatedGIF image as CLI
# To convert animated GIF to transparent background, amazing online tool :http://www.online-image-editor.com/

fullfile=$1
filename=$(basename "$fullfile")
extension="${filename##*.}"
filename="${filename%.*}"
res_folder=${filename}_data
rm -r $res_folder
mkdir $res_folder
cd $res_folder
convert ../$fullfile anime_${filename}_%d.png
nframes=`ls -l anime_${filename}_*.png | wc -l`
nframes=`expr $nframes - 1`

# create the xml file

xmlfile=anime_${filename}.xml
touch $xmlfile
echo '<?xml version="1.0" encoding="utf-8"?>' >> $xmlfile
echo '<animation-list xmlns:android="http://schemas.android.com/apk/res/android" android:oneshot="false">' >> $xmlfile
for (( i=0; i<=${nframes}; i++ ))
do
    echo '<item android:drawable="@drawable/anime_explode_'$i'" android:duration="50" />' >> $xmlfile
done

echo '</animation-list>' >> $xmlfile
