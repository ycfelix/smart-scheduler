import json
import pandas as pd
from difflib import SequenceMatcher
import random

test = '{ \
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


def get_raw_data(table, send_json_to_db, get_json_from_db):
    '''
    get raw data from db exclude the given user_id
    :param table: option for select table:str
    :param send_json_to_db: function, accept a str param
    :param get_json_from_db:function, return a str
    :return: db rows return type:list
    '''

    command = {
        "db_name": "Smart Scheduler",
        "sql_cmd": "SELECT * FROM dbo." + table}
    sql = json.dumps(command)

    # comment out these 2 for testing
    # send the sql to the database
    send_json_to_db(sql)
    # on database retrieve
    result = get_json_from_db()

    # uncomment this for testing
    # result=test
    return json.loads(result)['result']


def similar(a, b):
    result = []
    for row in a:
        result.append(SequenceMatcher(None, row, b).ratio())
    return result


def get_checklist_similar_user(user_id, n):
    '''
    get by same icon then similar title, if no ppl use same icon use title similarity
    :param user_id: str, the user id
    :param n: select top n closest user
    :return: list of user_id
    '''
    # please insert the function
    raw_data = get_raw_data("user_checklist", None, None)
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


def get_timetable_similar_user(user_id, n):
    '''
    get by same icon then similar title, if no ppl use same icon use title similarity
    :param user_id: str, the user id
    :param n: select top n closest user
    :return: list of user_id
    '''
    # please insert the function
    raw_data = get_raw_data("user_schedule", None, None)
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


test1 = get_checklist_similar_user(user_id='1', n=1)
test2 = get_timetable_similar_user(user_id='2', n=1)
print(test1)
print(test2)
