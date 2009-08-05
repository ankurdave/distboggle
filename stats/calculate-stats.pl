#!/usr/bin/perl

use warnings;

$mxt = $mxs = 0;
$mnt = $mns = 100000000;
while (<>) {
	next unless /^[A-Z]{16} (\d+) (\d+)$/;
	($s, $t) = ($1, $2);
	$tt += $t;
	$ts += $s;
	$mxt = $t if $t > $mxt;
	$mxs = $s if $s > $mxs;
	$mnt = $t if $t < $mnt;
	$mns = $s if $s < $mns;
	$fail++ if $s < 3500;
	last if ++$i >= 100;
}

$tt /= $i;
$ts /= $i;
$et = ($mxt - $mnt) / $i;
$es = ($mxs - $mns) / $i;
$fail /= $i;
$fail *= 100;
$success = 100 - $fail;

print "# of datapoints: $i\naverage time: $tt +- $et\naverage score: $ts +- $es\nfailure %: $fail\nsuccess %: $success\n";
