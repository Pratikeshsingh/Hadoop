
library(caret)
library(rpart.plot)

data_url <- c("https://archive.ics.uci.edu/ml/machine-learning-databases/car/car.data")
download.file(url = data_url, destfile = "car.data")

#import data
car_df <- read.csv("https://archive.ics.uci.edu/ml/machine-learning-databases/car/car.data", sep = ',', header = FALSE)
str(car_df)
summary(car_df)
#Spliting the DatasetR
set.seed(3033)
intrain <- createDataPartition(y = car_df$V7, p= 0.7, list = FALSE)
training <- car_df[intrain,]
testing <- car_df[-intrain,]
summary(car_df)

trctrl <- trainControl(method = "repeatedcv", number = 10, repeats = 3)
set.seed(3333)
dtree_fit <- train(V7 ~., data = training, method = "rpart",
                   parms = list(split = "information"),
                   trControl=trctrl,
                   tuneLength = 10)

dtree_fit
prp(dtree_fit$finalModel, box.palette = "Reds", tweak = 1.2)
testing[1,]
predict(dtree_fit, newdata = testing[1,])
test_pred <- predict(dtree_fit, newdata = testing)
confusionMatrix(test_pred, testing$V7 )  #check accuracy

##gini index
set.seed(3333)
dtree_fit_gini <- train(V7 ~., data = training, method = "rpart",
                          parms = list(split = "gini"),
                          trControl=trctrl,
                          tuneLength = 10)
dtree_fit_gini
prp(dtree_fit_gini$finalModel, box.palette = "Blues", tweak = 1.2)


test_pred_gini <- predict(dtree_fit_gini, newdata = testing)
confusionMatrix(test_pred_gini, testing$V7 )  #check accuracy

#The above Decision Tree can be made using entropy as below:
dtree_fit_entropy= train(V7~.,data=training,method = 'rpart',parms = 
                           list(split="entropy"),trControl=trctrl,tuneLength=10)
prp(dtree_fit_entropy$finalModel,box.palette = "Greens",tweak=1.2)
