import json
import pandas as pd
from difflib import SequenceMatcher
import random

test = '{ \
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

def get_raw_data(table, send_json_to_db, get_json_from_db):
    '''

    get raw data from db exclude the given user_id
    :param table: option for select table:str
    :param send_json_to_db: function, accept a str param
    :param get_json_from_db:function, return a str
    :return: db rows return type:list
    '''
    command = {
        "db_name":"Smart Scheduler",
        "sql_cmd": "SELECT * FROM dbo." + table}
    sql = json.dumps(command)

    send_json_to_db(sql)

    result = get_json_from_db()

    return json.loads(result)['result']


def similar(a,b):
    result = []
    for row in a:
        result.append(SequenceMatcher(None, row, b).ratio())
    return result

def get_calendar_similar_user(user_id,n):
    '''
    get by same icon then similar title, if no ppl use same icon use title similarity
    :param user_id: str, the user id
    :param n: select top n closest user
    :return: list of user_id
    '''
    # please insert the function
    raw_data = get_raw_data("Calendardata", None, None)
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

test1 = get_calendar_similar_user(user_id='1', n=1)
test2 = get_calendar_similar_user(user_id='2', n=1)
print(test1)
print(test2)
