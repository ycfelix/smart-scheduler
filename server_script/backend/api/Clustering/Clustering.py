import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
from backend.api.Clustering import helper
import requests
from heapq import nlargest
from sklearn.cluster import KMeans
from scipy.sparse import csr_matrix

#import User data and location data
def importData():
    #userIDSet=["0131412","0131412","2142144","0131412","0131412","0131412","1243551","3524541","2142144","1232144",
    #           "1232144","1232144","3524541","1243551","1243551","1243551","1232144","3524541","3524541","1232144",
    #           "3524541","2142144","2142144","0131412","2142144","2142144","2142144","2142144","0131412","2142144"]
    #latSet=[22.369453,22.364373,22.356991,22.372627,22.344501,22.351804,22.381649,22.366568,22.353709,22.379903,
    #        22.309567,22.313696,22.326400,22.323700,22.333545,22.313696,22.331163,22.347200,22.339261,22.327988,
    #        22.257332,22.250818,22.254949,22.277348,22.260192,22.288308,22.276395,22.306256,22.227302,22.238107,]
    #lngSet=[114.120113,114.122516,114.119254,114.108440,114.130891,114.096902,114.099477,114.150975,114.150117,114.109776,
    #        114.175008,114.165223,114.161618,114.188054,114.171574,114.179127,114.202645,114.182732,114.163678,114.195779,
    #        113.880496,113.922210,113.960834,113.915000,113.894572,113.981090,114.011646,113.918605,113.997741,113.926330]

    url = 'http://13.70.2.33/api/sql_db'
    myobj = {"db_name": "Smart Scheduler","sql_cmd": "SELECT * FROM user_location_history"}
    data = requests.post(url, data = myobj)
    dataJson=data.json()
    result=dataJson['result']
    userIDSet=[]
    latSet=[]
    lngSet=[]
    for i in range(len(result)):
        currentRecord=result[i]
        userIDSet.append(currentRecord['UserID'])
        latSet.append(currentRecord['Lat'])
        lngSet.append(currentRecord['Lng'])
    
    latlngData = np.column_stack([np.array(latSet), np.array(lngSet)])
    visualizeRawData(latlngData)
    
    return userIDSet,latSet,lngSet,latlngData

#visualize the data
def visualizeRawData(latlngData):
    fig, ax = plt.subplots()
    ax.plot(latlngData[:, 0], latlngData[:, 1], 'o', markersize=2, c='darkblue');

#try different numbers of cluster
def findBestnumbersOfcluster(latlngData):
    test_n_cluster = 20
    within_cluster_sum_squares = np.zeros(test_n_cluster)
    for i in range(test_n_cluster):
        n = i+1
        kmeans = KMeans(n_clusters = n, init = 'k-means++', max_iter=500, n_init=20, random_state = 0)
        kmeans.fit(latlngData)
        within_cluster_sum_squares[i] = kmeans.inertia_
    showTrendOfResult(within_cluster_sum_squares)
    visualizeTrend(within_cluster_sum_squares,test_n_cluster)
    
    return test_n_cluster

#show the trend of changing number of cluster    
def showTrendOfResult(within_cluster_sum_squares):
    np.set_printoptions(suppress=True)
    np.around(within_cluster_sum_squares, 2)

#visualize the trend   
def visualizeTrend(within_cluster_sum_squares,test_n_cluster):
    fig, ax = plt.subplots()
    ax.plot(within_cluster_sum_squares)
    xticks = np.arange(test_n_cluster)
    xticklabels = xticks + 1
    ax.set(xticks=xticks, xticklabels=xticklabels, title='The Elbow Method', 
        xlabel='Number of clusters, Picture 0', ylabel='WCSS');

#get the proper number of cluster
def ElbowMethod(latlngData, top_cluster_number, remark=True, init = 'k-means++', max_iter = 300, n_init = 10, random_state = None):
    #import numpy as np
    #from sklearn.cluster import KMeans
    wcss = np.zeros(top_cluster_number)
    
    for i in range(top_cluster_number):
        n = i+1
        kmeans = KMeans(n_clusters = n, init=init, max_iter=max_iter, n_init=n_init, random_state=random_state)
        kmeans.fit(latlngData)
        wcss[i] = kmeans.inertia_
    
    cosines = -1 * np.ones(top_cluster_number)
    
    for i in range(top_cluster_number-1):
    # check if the point is below a segment midpoint connecting its neighbors
        if (wcss[i] < (wcss[i+1]+wcss[i-1])/2 ):
            cosines[i]= (-1+(wcss[i-1]-wcss[i])*(wcss[i+1]-wcss[i]))/((1+(wcss[i-1]-wcss[i])**2)*(1+ (wcss[i+1]-wcss[i])**2))**.5
    
    # if remark:
    #     print("""Remark: If repeated applications(with different random_state parameters) yield different 
    #     values for the optimal number then K-means method does not work well on the array.""")
    
    return (np.flip(np.argsort(cosines))+1)

#convert userIDSet to numIdx
def userIDSetToNumIdx(userIDSet,latSet,lngSet):
    userIDBook=[]
    userIDIdxSet=[]
    for i in range(len(userIDSet)):
        if userIDSet[i] not in userIDBook:
            userIDBook.append(userIDSet[i])
        userIDIdxSet.append(userIDBook.index(userIDSet[i]))

    #form the dataset for clustering
    numOfUser=len(userIDBook)
    userIDIdx = pd.Series(userIDIdxSet)
    lat = pd.Series(latSet)
    lng = pd.Series(lngSet)
    d = {'UserID_index':userIDIdx, 'Lat': lat, 'Lng': lng}
    dataset = pd.DataFrame(d)
    
    return userIDBook,numOfUser,userIDIdx,lat,lng,d,dataset

#clustering using helper.py
def clustering(lat,lng,dataset):
    minLat=lat.min()
    maxLat=lat.max()
    minLng=lng.min()
    maxLng=lng.max()

    latRange=maxLat-minLat
    lngRange=maxLng-minLng
    lowerLat=minLat-latRange/2
    upperLat=maxLat+latRange/2
    lowerLng=minLng-lngRange/2
    upperLng=maxLng+lngRange/2
    helper.draw_scatterplot(dataset['Lat'],'Lat', dataset['Lng'], 'Lng', lowerLat, upperLat, lowerLng, upperLng)
    
    return lowerLat, upperLat, lowerLng, upperLng

#convert proper data type for clustering
def convertProperDataType(dataset,suggestedNumOfCluster,lowerLat, upperLat, lowerLng, upperLng):
    X = dataset[['Lat','Lng']].values

    #Create an instance of KMeans
    numOfClusters=suggestedNumOfCluster[0]
    kmeans_1 = KMeans(n_clusters=numOfClusters, random_state=0)
    #Use fit_predict to cluster the dataset
    predictions = kmeans_1.fit_predict(X)

    #check data value    
    visualizeClusteringResult(dataset,predictions,lowerLat, upperLat, lowerLng, upperLng)
    
    return predictions,numOfClusters
    
#visualize the result of clustering
def visualizeClusteringResult(dataset,predictions,lowerLat, upperLat, lowerLng, upperLng):
    helper.draw_clusters(dataset, predictions, 'Lat', 'Lng', lowerLat, upperLat, lowerLng, upperLng)

#count # of users appear in each cluster
def getClusteringResult(userIDIdx,numOfClusters,predictions):
    clusters=[]
    userIDArray = userIDIdx.to_numpy()
    for i in range(numOfClusters):
        clusters.append([])
    for i in range(len(predictions)):
        clusters[predictions[i]].append(userIDArray[i])
    
    return clusters

#get user avg occurence for calculation
def getUserAvgOccurence(numOfUser,numOfClusters,clusters):
    userAvgOccurrence=[]
    for i in range(numOfUser):
        currentUserOccurence=0
        for j in range(numOfClusters):
            currentUserOccurence+=clusters[j].count(i+1)
        userAvgOccurrence.append(currentUserOccurence/numOfClusters)
    
    return userAvgOccurrence


#functions
def covOfUserAB(A,B,clusters,userAvgOccurrence,numOfClusters):
    sONOOUAB=0 #sONOOUAB: sum of normalized occurrence of user A,B
    for i in range(numOfClusters):
        sONOOUAB+=(clusters[i].count(A)-userAvgOccurrence[A-1])*(clusters[i].count(B)-userAvgOccurrence[B-1])
    sONOOUAB=sONOOUAB/numOfClusters

    return sONOOUAB

def stdDevOfUser(A,clusters,userAvgOccurrence,numOfClusters):
    numerator=0
    for i in range(numOfClusters):
        numerator+=(clusters[i].count(A)-userAvgOccurrence[A-1])**2
    result=pow(numerator/numOfClusters,0.5)

    return result

def simOfUserAB(A,B,clusters,userAvgOccurrence,numOfClusters):
    numerator=covOfUserAB(A,B,clusters,userAvgOccurrence,numOfClusters)
    denominator=(stdDevOfUser(A,clusters,userAvgOccurrence,numOfClusters)*stdDevOfUser(B,clusters,userAvgOccurrence,numOfClusters))
    if(denominator==0): #means all data of A and B are the same
        result=-1.0
    else:
        result=numerator/denominator

    return result #result 1:similar, 0:no linear relationship, -1:opposite

#to find the distance matrix of all user
def getUserDistanceMatrix(numOfUser,clusters,userAvgOccurrence,numOfClusters):
    userDistanceMatrix=[]
    for i in range(numOfUser):
        userDistanceMatrix.append([])
        for j in range(numOfUser):
            if(i>j):
                userDistanceMatrix[i].append(userDistanceMatrix[j][i])
            if(i==j):
                userDistanceMatrix[i].append(-1.0)
            if(i<j):
                userDistanceMatrix[i].append(simOfUserAB(i,j,clusters,userAvgOccurrence,numOfClusters))

    return userDistanceMatrix

#get Nth similar Users
def getNthUserMostSimilarTotarget(userID,Nth):
    #get required data
    userIDSet,latSet,lngSet,latlngData=importData()
    test_n_cluster=findBestnumbersOfcluster(latlngData)
    suggestedNumOfCluster=ElbowMethod(latlngData, 20, max_iter=500, n_init=20)
    userIDBook,numOfUser,userIDIdx,lat,lng,d,dataset=userIDSetToNumIdx(userIDSet,latSet,lngSet)
    lowerLat, upperLat, lowerLng, upperLng=clustering(lat,lng,dataset)
    predictions,numOfClusters=convertProperDataType(dataset,suggestedNumOfCluster,lowerLat, upperLat, lowerLng, upperLng)
    clusters=getClusteringResult(userIDIdx,numOfClusters,predictions)
    
    #start calculate
    userAvgOccurrence=getUserAvgOccurence(numOfUser,numOfClusters,clusters)
    if userID in userIDBook:
        targetUserIdx=userIDBook.index(userID)

        similaritiesOfUserA=getUserDistanceMatrix(numOfUser,clusters,userAvgOccurrence,numOfClusters)[targetUserIdx].copy() #result 1:similar, 0:no linear relationship, -1:opposite
        idxForSimilaritiesOfUserA=[]
        for i in range(len(similaritiesOfUserA)):
            idxForSimilaritiesOfUserA.append(i)

        result=nlargest(Nth, idxForSimilaritiesOfUserA, key=lambda i: similaritiesOfUserA[i])

        #filter out -1 to 0 results
        #result 1:similar, 0:no linear relationship, -1:opposite
        result = [item for item in result if similaritiesOfUserA[item]>0.0]

        #get back the user ID of the result
        NthUser=[]
        for i in range(len(result)):
            #NthUser.append(idxOfUserID[result[i]])
            NthUser.append(userIDBook[result[i]])

        return NthUser
    else:
        return []

