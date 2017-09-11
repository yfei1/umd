
library(stringr)
tb=read.csv('C:/Users/Yufan/Downloads/DataMiner.csv')
partyPattern = '\\[([^[:digit:]])*\\]'
newCol1 = unlist(str_extract_all(tb[2][,],partyPattern))
newCol2 = unlist(str_extract_all(tb[3][,],partyPattern))

tb[2][,] <-gsub(' \\[([^[:digit:]])*\\]','',tb[2][,])
tb[3][,] <-gsub(' \\[([^[:digit:]])*\\]','',tb[3][,])

tb$WinnerParty <- newCol1
tb$OpponentParty <- newCol2

colnames(tb) <- c('Y','P' ,'MO', 'PV_W', 'PV_MO', 'EV_W','EV_MO','WP','OP')

for (i in 4:7) {
  tb[i][,] <-gsub('[^[:digit:]]*', '', tb[i][,])
}

write.csv(tb, 'C:/Users/Yufan/Desktop/CMSC424/PE.csv')


