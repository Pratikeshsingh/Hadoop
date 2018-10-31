credit = read.csv('/Users/pratikeshsingh/Desktop/R git/Credit Risk Data.csv', header = TRUE)
credit
str(credit)
summary(credit)
#check missing values
colSums(is.na(credit))
#boxplot for loss in thousands
boxplot(credit$Losses.in.Thousands, horizontal=TRUE)

hist(credit$Losses.in.Thousands)
#impute
  credit$Losses.in.Thousands.new=ifelse(credit$Losses.in.Thousands>=740,
                                        median(credit$Losses.in.Thousands),credit$Losses.in.Thousands)
hist(credit$Losses.in.Thousands.new)
boxplot(credit$Losses.in.Thousands.new,horizontal = TRUE)
dim(credit)
#transform using log
credit$Losses.in.Thousands.transformed = log(credit$Losses.in.Thousands)
boxplot(credit$Losses.in.Thousands.transformed,horizontal = TRUE)
hist(credit$Losses.in.Thousands.transformed)
credit$Losses.in.Thousands.transformed.sqrt = sqrt(credit$Losses.in.Thousands)
boxplot(credit$Losses.in.Thousands.transformed.sqrt,horizontal = TRUE)
hist(credit$Losses.in.Thousands.transformed.sqrt)
#correlation
names(credit)
str(credit)
credit2 = credit[,-c(1,5,6)]  # we are removing the factor variables as they are not required in correlation
round(cor(credit2),2)
model=lm(Losses.in.Thousands~Age+Years.of.Experience+Number.of.Vehicles+Gender+Married,credit)
model
vif(model)
summary(model)
predict(model,credit)
model.transformed.log=lm(Losses.in.Thousands.transformed~Age+Years.of.Experience+Number.of.Vehicles+Gender+Married,credit)
summary(model.transformed.log)
model.transformed.sqrt=lm(Losses.in.Thousands.transformed.sqrt~Age+Years.of.Experience+Number.of.Vehicles+Gender+Married,credit)
summary(model.transformed.sqrt)
model.new=lm(Losses.in.Thousands.new~Age+Years.of.Experience+Number.of.Vehicles+Gender+Married,credit)
summary(model.new)
plot(model)

