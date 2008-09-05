set term postscript eps enhanced color
set yrange [0:4000]
set xlabel "Mutation attempt"
set ylabel "Board Score"
set output "trials.eps"
plot "1.dat" with lines title "Trial 1: time = 82540 ms, final score = 3078", \
	"2.dat" with lines title "Trial 2: time = 49282 ms, final score = 2005", \
	"3.dat" with lines title "Trial 3: time = 104446 ms, final score = 2731"
