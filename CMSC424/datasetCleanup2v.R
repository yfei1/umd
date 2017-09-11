rm(list = ls())

library(rvest)
library(stringr)

tb <- "https://www.archives.gov/federal-register/electoral-college/scores.html" %>%
      read_html() %>% 
      html_nodes(xpath = '//tr/td/table') %>%
      .[[1]]

hPath <- c('//tr[1]/th[1]', 
          '//tr[2]/th',
          '//tr[3]/th',
          '//tr[4]/th',
          '//tr[4]/th',
          '//tr[5]/th',
          '//tr[5]/th',
          '//tr[6]/th',
          '//tr[7]/th',
          '//tr[8]/th')
tPath <- c('//tr[1]/th[2]', 
          '//tr[2]/td',
          '//tr[3]/td[1]',
          '//tr[4]/td[1]',
          '//tr[4]/td[2]',
          '//tr[5]/td[1]',
          '//tr[5]/td[2]',
          '//tr[6]/td',
          '//tr[7]/td',
          '//tr[8]/td')
dtset <- data.frame(index=1:53) 

#Election Column
head <- tb  %>%
  html_nodes(xpath = hPath[1]) %>%
  html_text(trim = TRUE)
text <- tb  %>%
  html_nodes(xpath = tPath[1]) %>%
  html_text(trim = TRUE)
colname <- head[1]
dtset[colname] <- text

#President Column
head <- tb  %>%
  html_nodes(xpath = hPath[2]) %>%
  html_text(trim = TRUE)
text <- tb  %>%
  html_nodes(xpath = tPath[2]) %>%
  html_text(trim = TRUE)

remove <- c("") #Used to Eliminate the extra "" col
head <- head[!head %in% remove]
text <- text[!text %in% remove]
colname <- head[1]
dtset[colname] <- text

#Main Opponent Column
head <- tb  %>%
  html_nodes(xpath = hPath[3]) %>%
  html_text(trim = TRUE)
text <- tb  %>%
  html_nodes(xpath = tPath[3]) %>%
  html_text(trim = TRUE)

pat = '([^[:digit:]])*\\]$'
head <- head[!head %in% remove]
text <- text[grep(pat, text)]
colname <- head[1]
dtset[colname] <- text

#Winner Electoral Column
head <- tb  %>%
  html_nodes(xpath = hPath[4]) %>%
  html_text(trim = TRUE)
text <- tb  %>%
  html_nodes(xpath = tPath[4]) %>%
  html_text(trim = TRUE)

head <- head[!head %in% remove]
text <- text[!text %in% remove] #Used to Eliminate the extra "" col
colname <- paste(head[1],"-Winner")
dtset[colname] <- text

#Opponent Electoral Column
head <- tb  %>%
  html_nodes(xpath = hPath[5]) %>%
  html_text(trim = TRUE)
text <- tb  %>%
  html_nodes(xpath = tPath[5]) %>%
  html_text(trim = TRUE)

head <- head[!head %in% remove]
text <- text[!text %in% remove] #Used to Eliminate the extra "" col
colname <- paste(head[1],"-Opponent")
dtset[colname] <- text

#Popular Vote Winner Column
head <- tb  %>%
  html_nodes(xpath = hPath[6]) %>%
  html_text(trim = TRUE)
text <- tb  %>%
  html_nodes(xpath = tPath[6]) %>%
  html_text(trim = TRUE)

pat = '\r'
head <- head[!head %in% remove]
text <- text[!grepl(pat,text)]
colname <- paste(head[1],"-Winner")
dtset[colname] <- text

#Popular Vote Opponent Column
head <- tb  %>%
  html_nodes(xpath = hPath[7]) %>%
  html_text(trim = TRUE)
text <- tb  %>%
  html_nodes(xpath = tPath[7]) %>%
  html_text(trim = TRUE)

pat = 'Return to Index'
head <- head[!head %in% remove]
text <- c(rep("no record", time=9), text[!grepl(pat,text)])
colname <- paste(head[1],"-Opponent")
dtset[colname] <- text

#Vote for Others Column
head <- tb  %>%
  html_nodes(xpath = hPath[8]) %>%
  html_text(trim = TRUE)
text <- tb  %>%
  html_nodes(xpath = tPath[8]) %>%
  html_text(trim = TRUE)

pat = 'Votes for Others'

head <- head[!head %in% remove]
text <- text[!text %in% remove]
condition <- !grepl(pat,head)
copy <-text
text[condition] <- 'NA'

colname <- head[1]
dtset[colname] <- text

#Vice President Column
head <- tb  %>%
  html_nodes(xpath = hPath[9]) %>%
  html_text(trim = TRUE)
text <- tb  %>%
  html_nodes(xpath = tPath[9]) %>%
  html_text(trim = TRUE)

pat = 'Vice President'
rmpat = 'Notes|(Return to Index)'
head <- head[!head %in% remove]
text <- text[!text %in% remove]
text <- text[!grepl(rmpat,text)]
head[condition] <- pat
prev <- text
text[condition] <- copy[condition]

colname <- head[1]
dtset[colname] <- text


#Further Cleanse up
dtset <- dtset[!names(dtset) %in% 'index']
partyPattern = '\\[([^[:digit:]])*\\]'
newCol1 = unlist(str_extract_all(dtset[2][,],partyPattern))
newCol2 = unlist(str_extract_all(dtset[3][,],partyPattern))

newCol1 <- gsub('(\\[)|(\\])','',newCol1)
newCol2 <- gsub('(\\[)|(\\])','',newCol2)

dtset[2][,] <-gsub(' \\[([^[:digit:]])*\\]','',dtset[2][,])
dtset[3][,] <-gsub(' \\[([^[:digit:]])*\\]','',dtset[3][,])

dtset$WinnerParty <- newCol1
dtset$OpponentParty <- newCol2

for (i in 4:7) {
  dtset[i][,] <- gsub('[^[:digit:]]*', '', dtset[i][,])
}

vpevpat <- ' (\\([0-9]*\\))'
vpev <- unlist(str_extract_all(dtset[9][,],vpevpat)) %>%
        gsub(pattern='(\\()|(\\))',replacement = '')
vpev <- c(rep('',time = 4),vpev)
dtset[9][,] <- gsub(' \\([0-9]*\\)', '', dtset[9][,])
dtset$VicePresidentEV <- vpev

#Candidate Pool Dataset
data.frame(dtset$Election ,dtset$`Votes for Others`)
ele <- dtset$Election
cpYr <- c()
cpLst <- c()
cpVote <- c()

for (i in 1:length(ele)) {
  sigYrVote <- dtset$`Votes for Others`[i]
  pat <- '[:alpha:]([:alpha:]| |\\.)*[:alpha:] \\([0-9]*\\)'
  if (grepl('^NA$', sigYrVote)) {
    cpYr <- c(cpYr, ele[i]) 
    cpLst <- c(cpLst, '')
    cpVote <- c(cpVote, '')
  } else {
    temp <- unlist(str_extract_all(sigYrVote, pat)) 
    sigYrVote <- temp %>% gsub(pattern=vpevpat, replacement='')
    vote <- temp %>% gsub(pattern='[^0-9]', replacement='')
    cpYr <- c(cpYr, rep(ele[i], length(sigYrVote)))
    cpLst <- c(cpLst, sigYrVote)
    cpVote <- c(cpVote, vote)
  }
}

nameReg <- '([A-Z]\\. )+'
cpLst <-gsub(nameReg,'',cpLst)

dtset$President <- gsub(nameReg,'',dtset$President)
dtset$'Main Opponent' <- gsub(nameReg,'',dtset$'Main Opponent')
dtset$`Vice President` <- gsub(nameReg,'',dtset$`Vice President`)


dtset <- dtset[!names(dtset) %in% 'Votes for Others']
data.frame(Election=cpYr, 'CandidateList'=cpLst, 'CandidateVote'=cpVote) %>% write.csv(row.names=FALSE, file='C:/Users/Yufan/Desktop/CMSC424/CandidatePool.csv')
dtset %>% write.csv(row.names=FALSE, file='C:/Users/Yufan/Desktop/CMSC424/ElectoralVoteDataSet.csv')

