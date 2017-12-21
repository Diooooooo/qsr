#!/bin/bash
echo $1 $2 $3 $4
workdir=$1
srcfile=$2
dstfile=$3
dstfile_zip=$3".zip"
cp=/bin/cp
mv=/bin/mv

emptydir="assets/images/_e9w_"$4

cd $workdir
$cp $srcfile $dstfile_zip

mkdir -p $emptydir
zip $dstfile_zip $emptydir
$mv $dstfile_zip $dstfile
rm -rf $emptydir


