# api utils
from flask import request, abort
from difflib import SequenceMatcher
from backend.config import config
from backend.api.Clustering.Clustering import getNthUserMostSimilarTotarget as map_similar
import itertools, pyodbc, json, random, pandas as pd
# import os

'''
test data
'''
test_ttb_chklist = '{ \
    "type": "query", \
    "result": [ \
        { \
            "class_title": "good_class", \
            "user_id": "2", \
            "day_of_week": 1, \
            "icon": 45745, \
            "title": "bygsdgs!" \
        }, \
        { \
            "class_title": "good_class", \
            "user_id": "4", \
            "day_of_week": 6, \
            "icon": 1234, \
            "title": "drfyr!" \
        }, \
        { \
            "class_title": "sad_class", \
            "user_id": "3", \
            "day_of_week": 3, \
            "icon": 1234, \
            "title": "sdfgdcvbt!" \
        }, \
        { \
            "class_title": "happy_class", \
            "user_id": "1", \
            "day_of_week": 3, \
            "icon": 299, \
            "title": "fgert34!" \
        } \
    ] \
}'

test_cldr = '{ \
    "type": "query", \
    "result": [ \
        { \
            "EventId": "1", \
            "user_id": "2", \
            "Time": "10:20 PM", \
            "Date": "2020-04-09", \
            "EventMonth": "April", \
            "EventYear": "2020", \
            "Notify": "off", \
            "Type": "Exercise", \
            "Event": "bygsdgs!" \
        }, \
        { \
            "EventId": "2", \
            "user_id": "4", \
            "Time": "10:20 PM", \
            "Date": "2020-04-09", \
            "EventMonth": "April", \
            "EventYear": "2020", \
            "Notify": "off", \
            "Type": "Study", \
            "Event": "drfyr!" \
        }, \
        { \
            "EventId": "3", \
            "user_id": "3", \
            "Time": "10:20 PM", \
            "Date": "2020-04-09", \
            "EventMonth": "April", \
            "EventYear": "2020", \
            "Notify": "off", \
            "Type": "Study", \
            "Event": "fgert34!" \
        }, \
        { \
           "EventId": "4", \
            "user_id": "1", \
            "Time": "10:20 PM", \
            "Date": "2020-04-09", \
            "EventMonth": "April", \
            "EventYear": "2020", \
            "Notify": "off", \
            "Type": "Study", \
            "Event": "fgert34!" \
        } \
    ] \
}'

'''
server connection helpers
'''

def getJSON():
	print (request.base_url)
	if (type(request.get_json()).__name__ == 'NoneType'):
		result = {}
		result['db_name'] = request.values.get('db_name')
		result['sql_cmd'] = request.values.get('sql_cmd')
		result['user_id'] = request.values.get('user_id')
		result['num_user'] = request.values.get('num_user')
		return result
	else:
		return request.get_json()

def getCnxn(db_name, port=1433):
	# server = os.environ.get['FYPDB_SERVER']
	# username = os.environ.get['FYPDB_USERNAME']
	# password = os.environ.get['FYPDB_PWD']
	# driver = os.environ.get['FYPDB_DRIVER']
	server = config.get('FYPDB_SERVER')
	username = config.get('FYPDB_USERNAME')
	password = config.get('FYPDB_PWD')
	driver = config.get('FYPDB_DRIVER')
	
	return pyodbc.connect('DRIVER='+driver+';SERVER='+server+\
		';PORT=' + str(port) + ';DATABASE='+db_name+';UID='+username+\
		';PWD='+ password)

def getCursor(cursor, cmd):
	return cursor.execute(cmd)

def getServerReturnValue(cursor):
	return cursor.fetchall()

'''
Distance Matrix functions and helpers
'''
def resultToListOfDict(result, col_names):
	temp_list = []
	for row in result:
		temp = {}
		for (key, value) in itertools.zip_longest(col_names, row):
			if (type(value).__name__ == "Decimal"):
				temp[str(key)] = float(round(value, 2))
			else:
				temp[str(key)] = value
		temp_list.append(temp)

	return temp_list

def get_raw_data(table, send_json_to_db, get_json_from_db):
    '''
    get raw data from db exclude the given user_id
    :param table: option for select table:str
    :param send_json_to_db: function, accept a str param
    :param get_json_from_db:function, return a str
    :return: db rows return type:list
    '''

    # real code
    command = {}
    command['db_name'] = "Smart Scheduler"
    command['sql_cmd'] = "SELECT * FROM dbo." + table

    # get database connection and cursor
    try:
	    cnxn = getCnxn(command['db_name'])
	    cursor = cnxn.cursor()

	    # on database retrieve
	    cursor = send_json_to_db(cursor, command['sql_cmd'])
	    col_names = [col[0] for col in cursor.description]
	    result = get_json_from_db(cursor)
	    result = resultToListOfDict(result, col_names)
	    # print(result)

	    # close cursor and connection
	    cursor.close()
	    cnxn.close()
	    # print ('type of result = ' + type(result).__name__)
	    return result

    except pyodbc.Error as pex:
    	abort(500)

    # testing code, commment them if unused.
    # result = test_cldr if table == 'Calendardata' else test_ttb_chklist
    # print ('type of result = ' + type(json.loads(result)['result']).__name__)
    # print (json.loads(result)['result'])
    # return json.loads(result)['result']

def similar(a, b):
    result = []
    for row in a:
        result.append(SequenceMatcher(None, row, b).ratio())
    return result

def get_map_similar(userID, n):
	return map_similar(userID, n)

def get_timetable_similar_user(user_id, n):
    '''
    get by same icon then similar title, if no ppl use same icon use title similarity
    :param user_id: str, the user id
    :param n: select top n closest user
    :return: list of user_id
    '''
    # please insert the function
    raw_data = get_raw_data("user_schedule", getCursor, getServerReturnValue)
    df = pd.DataFrame(raw_data)
    me = df[df['user_id'] == user_id]
    # randomly select one of my checklist item

    me = me.iloc[random.randint(0, me.shape[0] - 1)]
    other = df[df['user_id'] != user_id]

    # user that use the same day of week
    same_day = other[other['day_of_week'] == me['day_of_week']].copy()
    if same_day.shape[0] is 0:
        # no one use your same icon, use default all ppl
        same_day = other.copy()

    # calculate similarity between 2 users' title
    same_day["title_score"] = similar(same_day['class_title'], me['class_title'])
    sorted_user = same_day.sort_values(by=["title_score"], ascending=False)
    return sorted_user.iloc[:n]['user_id'].tolist()

def get_checklist_similar_user(user_id, n):
    '''
    get by same icon then similar title, if no ppl use same icon use title similarity
    :param user_id: str, the user id
    :param n: select top n closest user
    :return: list of user_id
    '''
    # please insert the function
    raw_data = get_raw_data("user_checklist", getCursor, getServerReturnValue)
    df = pd.DataFrame(raw_data)
    me = df[df['user_id'] == user_id]
    # randomly select one of my checklist item

    me = me.iloc[random.randint(0, me.shape[0] - 1)]
    other = df[df['user_id'] != user_id]

    # user that use the same icon
    same_icon = other[other['icon'] == me['icon']]
    if same_icon.shape[0] is 0:
        # no one use your same icon, use default all ppl
        same_icon = other.copy()

    # calculate similarity between 2 users' title
    same_icon["title_score"] = similar(same_icon['title'], me['title'])
    sorted_user = same_icon.sort_values(by=["title_score"], ascending=False)
    return sorted_user.iloc[:n]['user_id'].tolist()

def get_calendar_similar_user(user_id,n):
    '''
    get by same icon then similar title, if no ppl use same icon use title similarity
    :param user_id: str, the user id
    :param n: select top n closest user
    :return: list of user_id
    '''
    # please insert the function
    raw_data = get_raw_data("Calendardata", getCursor, getServerReturnValue)
    df = pd.DataFrame(raw_data)
    me = df[df['user_id'] == user_id]
    # randomly select one of my calendar item
    
    me = me.iloc[random.randint(0, me.shape[0] - 1)]
    other = df[df['user_id'] != user_id]

    
    # user that use the same type
    same_type = other[other['Type'] == me['Type']]
    if same_type.shape[0] is 0:
        #no one user your same type, user default all ppl
        same_type = other.copy()

    # calculate similarity between 2 users' eventname
    same_type["title_score"] = similar(same_type['Event'],me['Event'])
    sorted_user = same_type.sort_values(by=["title_score"], ascending = False)
    return sorted_user.iloc[:n]['user_id'].tolist()