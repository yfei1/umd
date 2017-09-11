rm(list = ls())
getexpr = function(s,g)substring(s,g,g+attr(g,"match.length")-1)
thepage <- readLines('https://en.wikipedia.org/wiki/Historical_polling_for_U.S._Presidential_elections')

tbregexp <- '<table class="wikitable">'
tbBlock <- grep(tbregexp, thepage)

regExp <- c('<caption><b><a href=.* title=.*>([0-9]*)</a>', #year
            '<th>(.*) \\(.*\\).*</th>',  #candidate name
            '<td><b>Actual result</b></td>', #month name
            '<tr style="background:#9bddff;">', #val range
            '<td>[^0-9]*([0-9]+)%.*</td>')

totalYr <- c()
totalCand <- c()
totalRt <- c()

for(i in 1:length(tbBlock)) {
  of <-tbBlock[i]
  if (i == length(tbBlock))
    ot <- 2400
  else ot <-tbBlock[i+1]
  
  yearlines <- grep(regExp[1], thepage[of:ot],value=TRUE)
  gg <- gregexpr(regExp[1], yearlines)
  matches <- mapply(getexpr,yearlines,gg)
  year <-gsub(regExp[1],'\\1',matches)
  
  candidatelines <- grep(regExp[2], thepage[of:ot],value=TRUE)
  gg <- gregexpr(regExp[2], candidatelines)
  matches <- mapply(getexpr,candidatelines,gg)
  cand <-gsub(regExp[2],'\\1',matches)
  
  datalines <- grep(regExp[4], thepage[of:ot]) #search within the block
  from <- tbBlock[i]+datalines[1]
  to <- tbBlock[i]+datalines[2]
  reslines <- grep(regExp[5],thepage[from:to],value=TRUE)
  gg <- gregexpr(regExp[5], reslines)
  matches <- mapply(getexpr,reslines,gg)
  valRes <-gsub(regExp[5],'\\1',matches)
  
  totalYr <- c(totalYr, rep(year,length(cand)))
  totalCand <- c(totalCand, cand)
  totalRt <- c(totalRt, valRes)
}

candNameReg <- '<a .*>([^<]*)</a>'
linesToReplaced <- grep(candNameReg, totalCand)
for(i in 1:length(linesToReplaced)) {
  candLines <- grep(candNameReg, totalCand[linesToReplaced[i]],value=TRUE)
  gg <- gregexpr(candNameReg, candLines)
  matches <- mapply(getexpr,candLines,gg)
  totalCand[linesToReplaced[i]] <-gsub(candNameReg,'\\1',matches)
}

candNameReg <- '([A-Z]\\. )+'
totalCand <-gsub(candNameReg,'',totalCand)

df <- data.frame(Election = totalYr, cand = totalCC:/Users/Yufanand, pollrate= totalRt)
write.csv(df, row.names=FALSE, file='/Desktop/CMSC424/PollResult.csv')
