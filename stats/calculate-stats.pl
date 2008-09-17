while (<>) {
	next unless /^[A-Z]{16} (\d+) (\d+)$/;
	($s, $t) = ($1, $2);
	$tt += $t;
	$ts += $s;
	$i++;
}

$tt /= $i;
$ts /= $i;

print "$i $tt $ts\n";
