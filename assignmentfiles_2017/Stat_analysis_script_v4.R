###############bent cigar##############################

bent_lira <- read.csv('bent_lira.csv', sep = ';')
bent_roro <- read.csv('bent_roro.csv', sep = ';')
bent_tour <- read.csv('bent_tour.csv', sep = ';')
bent_trun <- read.csv('bent_trun.csv', sep = ';')
bent_rand <- read.csv('bent_rand.csv', sep = ';')
bent_combined <- data.frame(group = rep(c("Lin.Rank", "R.Robin", "Tournament", "Elitism","Random"), each = 200),  fitness = c(bent_lira$score,bent_roro$score,bent_tour$score,bent_trun$score,bent_rand$score))

#significance tests
fitness.fisher  = aov(fitness/mean(fitness)~group,data=bent_combined)
fitness.welch   = oneway.test(fitness~group,data=bent_combined)
fitness.kruskal = kruskal.test(fitness~group,data=bent_combined)
summary(fitness.fisher)
print(fitness.welch)
print(fitness.kruskal)

#residual plot
mean_group   = tapply(1/sqrt(bent_combined$fitness),bent_combined$group,mean)
bent_combined$resid   = (1/sqrt(bent_combined$fitness) - mean_group[as.numeric(bent_combined$group)])
col_group = rainbow(nlevels(bent_combined$group))
qqnorm(bent_combined$resid,col=col_group[as.numeric(bent_combined$group)])
legend(-0.7,-1.5,legend=levels(bent_combined$group),col=col_group,pch=21,ncol=2,box.lwd=NA,cex=0.8)

hist((bent_lira$score), 20 ,xlab = 'Fitness', main = 'Distribution of data',col= 'grey')


#is random mean the same as sum of other means
mean_random <- mean_group[c('Random')]
est_mean_random <- (mean_group[c('Lin.Rank')]+mean_group[c('Elitism')]+mean_group[c('R.Robin')]+mean_group[c('Tournament')])/4

t.test(bent_rand$score,(bent_lira$score+bent_roro$score+bent_tour$score+bent_trun$score)/4)

norma_rand <- bent_rand$score/mean(bent_rand$score)
norma_est <- (bent_lira$score+bent_roro$score+bent_tour$score+bent_trun$score)/(mean(bent_lira$score+bent_roro$score+bent_tour$score+bent_trun$score))

t.test(norma_rand,norma_est)



##########################schaffers#########################

scha_lira <- read.csv('scha_lira.csv', sep = ';')
scha_roro <- read.csv('scha_roro.csv', sep = ';')
scha_tour <- read.csv('scha_tour.csv', sep = ';')
scha_trun <- read.csv('scha_trun.csv', sep = ';')
scha_rand <- read.csv('scha_rand.csv', sep = ';')
scha_combined <- data.frame(group = rep(c("Lin.Rank", "R.Robin", "Tournament", "Elitism","Random"), each = 200),  fitness = c(scha_lira$score,scha_roro$score,scha_tour$score,scha_trun$score,scha_rand$score))

#significance tests
fitnessscha.fisher  = aov(log(fitness)~group,data=scha_combined)
fitnessscha.welch   = oneway.test(fitness~group,data=scha_combined)
fitnessscha.kruskal = kruskal.test(fitness~group,data=scha_combined)
summary(fitnessscha.fisher)
print(fitnessscha.welch)
print(fitnessscha.kruskal)

#is random mean of others
t.test(scha_rand$score,(scha_lira$score+scha_roro$score+scha_tour$score+scha_trun$score)/4)

norma_rand_scha <- scha_rand$score/mean(scha_rand$score)
norma_est_scha <- (scha_lira$score+scha_roro$score+scha_tour$score+scha_trun$score)/(mean(scha_lira$score+scha_roro$score+scha_tour$score+scha_trun$score))

t.test(norma_rand_scha,norma_est_scha)

#test of mean trick works everywhere
t.test(scha_roro$score,(scha_lira$score+scha_rand$score+scha_tour$score+scha_trun$score)/4)

mean(scha_rand$score)
mean(cbind(scha_lira$score+scha_roro$score+scha_tour$score+scha_trun$score)/4)


#############combined plots#################################################

#boxplots for schaffers and bent cigar
par(mfrow=c(1,2),mar=c(7,5,2,1),oma = c(0, 0, 1.4, 0))
boxplot(bent_combined$fitness~bent_combined$group,outline = FALSE, col = 'grey',main = 'Bent Cigar', las=2)
boxplot(scha_combined$fitness~scha_combined$group,outline = FALSE, col = 'grey',main = 'Schaffers',las=2)
mtext('Mean fitness for several selection operators', outer = TRUE, cex = 1.3)

#best fitness plots
all_best_bent <- data.frame(Linear.Ranking = as.numeric(bent_lira[1,3:12]),Round.Robin= as.numeric(bent_roro[1,3:12]),Tournament = as.numeric(bent_tour[1,3:12]),Elitism = as.numeric(bent_trun[1,3:12]), Random = as.numeric(bent_rand[1,3:12]))
all_best_scha <- data.frame(Linear.Ranking = as.numeric(scha_lira[1,3:12]),Round.Robin= as.numeric(scha_roro[1,3:12]),Tournament = as.numeric(scha_tour[1,3:12]),Elitism = as.numeric(scha_trun[1,3:12]), Random = as.numeric(scha_rand[1,3:12]))

par(mfrow=c(1,2),mar=c(7,5,2,1),oma = c(0, 0, 1.4, 0))
matplot(all_best,pch=1,col=c(1,2,3,4,7),lty ="solid", type = 'b', main = 'Bent Cigar',xlab = 'Timepoint', ylab = 'Fitness') #plot
legend("bottomright", legend = levels(bent_combined$group), col=c(1,2,3,4,7), pch=1,cex=0.6) 
matplot(all_best_scha,pch=1,col=c(1,2,3,4,7),lty ="solid", type = 'b', main = 'Schaffers',xlab = 'Timepoint', ylab = 'Fitness') #plot
legend("bottomright", legend = levels(bent_combined$group), col=c(1,2,3,4,7), pch=1,cex=0.5) 
mtext('Best fitness per selection operator over time', outer = TRUE, cex = 1.3)


