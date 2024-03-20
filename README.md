An idea for a mock programming language implementation akin to Open Computers and TIS-3D

The language itself is mostly based on Stationeer's [MIPS](https://stationeers-wiki.com/MIPS) language

An example of what the language should hopefully look like when written:

```
alias main r0
alias setting d00
alias power 93.65
alias alert cFF

# Load d00 * 93.65 into r0
mul main setting power
# Square r0
pow main main 2
# Load r0 / 93.65 into c8
div alert main power
```