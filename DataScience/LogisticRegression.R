bank_data=read.csv('/Users/pratikeshsingh/Desktop/R git/BANK LOAN.csv')
#use the path of the BANK LOAN.csv available in this folder
str(bank_data)
names(bank_data)
summary(bank_data)

#model building
riskmodel<-glm(DEFAULTER~AGE+EMPLOY+ADDRESS+DEBTINC+CREDDEBT+OTHDEBT,family=binomial,data=bank_data)
summary(riskmodel)               

#global testing 
null<-glm(DEFAULTER ~ 1, family=binomial,data=bank_data) 
anova(null,riskmodel, test="Chisq")

#predicting probabilities
bank_data$predprob<-round(fitted(riskmodel),2)
head(bank_data)

#classification table
table(bank_data$DEFAULTER,fitted(riskmodel)>0.5)


#ROC
install.packages("ROCR")
library(ROCR) 
bank_data$predprob<-fitted(riskmodel)
pred<-prediction(bank_data$predprob,bank_data$DEFAULTER)
perf<-performance(pred,"tpr","fpr")
plot(perf)
abline(0,1)

