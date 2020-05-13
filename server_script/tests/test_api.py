import json, pytest
from backend.api.utils import getCnxn

cnxn = None
cursor = None
cmd = None

def setup_module():
    """ setup any state specific to the execution of the given module."""
    print()
    print('--- setup in module level, test_api.py ---')
    global cnxn
    global cursor
    global cmd
    cnxn = getCnxn('TestData')
    cursor = cnxn.cursor()
    cmd = [
    '''
    create table TestAccounts (
    	UserID varchar(255),
    	Email varchar(255),
    	HashedPwd varchar(255),
    	primary key (UserID, Email)
    )
    ''',
    '''
    insert into TestAccounts values
    ('1234', 'test@dummyEmail.com', 'hahayoyo!'),
    ('2345', 'test2@dummyEmail.com', 'hehehaha~'),
    ('721831', 'WorldWarIII@doodleMail.com', 'testing12345')
    ''']
    for cmd_seg in cmd:
    	cursor.execute(cmd_seg)

    cnxn.commit()

def teardown_module():
    """ teardown any state that was previously setup with a setup_module
    method.
    """
    print()
    print('--- teardown in module level, test_api.py ---')
    global cnxn
    global cursor
    global cmd
    cmd = '''
    drop table TestAccounts
    '''
    cursor.execute(cmd)
    cnxn.commit()
    cursor.close()
    cnxn.close()

class TestSQLAPI:

	@pytest.mark.parametrize("test_json, status_code, error_msg",
		[({}, 400, 'Database name field does not exist (db_name)'), 
		({"db_name": ""}, 400, 'SQL command field does not exist (sql_cmd)'),
		({"sql_cmd": ""}, 400, 'Database name field does not exist (db_name)'),
		({"db_name": "", "sql_cmd": ""}, 400, 'Database name is not specified'),
		({"db_name": "TestData", "sql_cmd": ""}, 400, 'SQL command is not specified'),
		({"db_name": "", "sql_cmd": "select Email from TestAccounts"}, 400, 'Database name is not specified')])
	def test_query_check_miss_param(self, client, test_json, status_code, error_msg):
		rv = client.post('/api/sql_db', json=json.dumps(test_json))
		assert rv.status_code == status_code
		assert json.loads(rv.data.decode('utf-8'))['error_msg'] == error_msg

	@pytest.mark.parametrize("test_json, num_result, expect_type",
		[
		({"db_name": "TestData", "sql_cmd": "select * from TestAccounts"}, 3, 'query'),  
		({"db_name": "TestData", "sql_cmd": "select * from TestAccountsssss"}, -1, 'query'),
		({"db_name": "TestData", "sql_cmd": "select * from TestAccounts where UserID='721831'"}, 1, 'query'),
		({"db_name": "TestData", "sql_cmd": "select * from TestAccounts where UserID='2345'"}, 1, 'query'),
		({"db_name": "TestData", "sql_cmd": "select * from TestAccounts where UserID='1234'"}, 1, 'query'),
		({"db_name": "TestData", "sql_cmd": "select * from TestAccounts where UserID='721831!'"}, 0, 'query')
		])
	def test_query_different_params(self, client, test_json, num_result, expect_type):
		rv = client.post('/api/sql_db', json=json.dumps(test_json))
		assert json.loads(rv.data.decode('utf-8'))['num_result'] == num_result
		assert json.loads(rv.data.decode('utf-8'))['type'] == expect_type

	@pytest.mark.parametrize("test_json, expect_type",
		[
		({"db_name": "TestDatasss", "sql_cmd": "select * from TestAccounts"}, 'pyodbc error'),
		({"db_name": "TestData", "sql_cmd": "* select from TestAccounts"}, 'unsupported'),
		({"db_name": "TestData", "sql_cmd": "select * from TestAccounts where yoyoid=1234"}, 'query')
		])
	def test_query_error_operation(self, client, test_json, expect_type):
		rv = client.post('/api/sql_db', json=json.dumps(test_json))
		assert json.loads(rv.data.decode('utf-8'))['type'] == expect_type

	@pytest.mark.parametrize("test_json, expected_result, num_rows_affected",
		[({"db_name": "TestData", "sql_cmd": "insert into TestAccounts values ('ba13d0t', 'yolo@gmail.com', 'ILoveMyFYP!')"}, True, 1), 
		({"db_name": "TestData", "sql_cmd": "insert into TestAccounts values ('ba13d0t', 'yolo@gmail.com', 'ILoveMyFYP!')"}, False, 0), 
		({"db_name": "TestData", "sql_cmd": "insert into TestAccountsssss values ('wuNwaMwuSwaL', 'fallout@ymail.com', 'mybirthday')"}, False, 0),
		({"db_name": "TestData", "sql_cmd": "insert into TestAccounts values ('wuNwaMwuSwaL', 'fallout@ymail.com', 'mybirthday')"}, True, 1),
		({"db_name": "TestData", "sql_cmd": "insert into TestAccounts valuess ('wuNwaMwuSwaL', 'fallout@ymail.com', 'mybirthday')"}, False, 0)])
	def test_insert_records(self, client, test_json, expected_result, num_rows_affected):
		rv = client.post('/api/sql_db', json=json.dumps(test_json))
		assert json.loads(rv.data.decode('utf-8'))['result'] == expected_result
		assert json.loads(rv.data.decode('utf-8'))['num_rows_affected'] == num_rows_affected

	@pytest.mark.parametrize("test_json, expect_num_records",
		[({"db_name": "TestData", "sql_cmd": "select UserID from TestAccounts where UserID='ba13d0t'"}, 1),
		({"db_name": "TestData", "sql_cmd": "select UserID from TestAccounts where UserID='wuNwaMwuSwaL'"}, 1),
		({"db_name": "TestData", "sql_cmd": "select UserID from TestAccounts where UserID='721831'"}, 1),
		({"db_name": "TestData", "sql_cmd": "select UserID from TestAccounts where UserID<>'1234'"}, 4)])
	def test_query_after_insertion(self, client, test_json, expect_num_records):
		rv = client.post('/api/sql_db', json=json.dumps(test_json))
		assert json.loads(rv.data.decode('utf-8'))['num_result'] == expect_num_records

	@pytest.mark.parametrize("test_json, expected_result, num_rows_affected",
	[({"db_name": "TestData", "sql_cmd": "delete from TestAccounts where UserID='ba13d0t'"}, True, 1), 
	({"db_name": "TestData", "sql_cmd": "delete from TestAccounts where UserID='ba13d0t'"}, True, 0), 
	({"db_name": "TestData", "sql_cmd": "delete from TestAccountsssss where UserID='wuNwaMwuSwaL'"}, False, 0),
	({"db_name": "TestData", "sql_cmd": "delete from TestAccounts where UserID='wuNwaMwuSwaL'"}, True, 1),
	({"db_name": "TestData", "sql_cmd": "delete from TestAccounts where UserID='2345'"}, True, 1)])
	def test_delete_records(self, client, test_json, expected_result, num_rows_affected):
		rv = client.post('/api/sql_db', json=json.dumps(test_json))
		assert json.loads(rv.data.decode('utf-8'))['result'] == expected_result
		assert json.loads(rv.data.decode('utf-8'))['num_rows_affected'] == num_rows_affected

	@pytest.mark.parametrize("test_json, expect_num_records",
		[({"db_name": "TestData", "sql_cmd": "select UserID from TestAccounts where UserID='ba13d0t'"}, 0),
		({"db_name": "TestData", "sql_cmd": "select UserID from TestAccounts where UserID='wuNwaMwuSwaL'"}, 0),
		({"db_name": "TestData", "sql_cmd": "select UserID from TestAccounts where UserID='721831'"}, 1),
		({"db_name": "TestData", "sql_cmd": "select UserID from TestAccounts where UserID='1234'"}, 1),
		({"db_name": "TestData", "sql_cmd": "select UserID from TestAccounts where UserID='2345'"}, 0)])
	def test_query_after_deletion(self, client, test_json, expect_num_records):
		rv = client.post('/api/sql_db', json=json.dumps(test_json))
		assert json.loads(rv.data.decode('utf-8'))['num_result'] == expect_num_records

class TestDistMatrix:

	@pytest.mark.parametrize("test_json, status_code, error_msg",
		[({}, 400, 'user_id, num_user'), 
		({"user_id": ""}, 400, 'user_id, num_user'),
		({"num_user": ""}, 400, 'user_id, num_user'),
		({"user_id": "", "num_user": ""}, 400, 'user_id, num_user'),
		({"user_id": 1, "num_user": ""}, 400, 'num_user'),
		({"user_id": "", "num_user":3}, 400, 'user_id')])
	def test_dist_matrix_check_error_input(self, client, test_json, status_code, error_msg):
		rv = client.post('/api/distance_matrix/0', json=json.dumps(test_json))
		assert rv.status_code == status_code
		assert json.loads(rv.data.decode('utf-8'))['type'] == 'missing parameter(s):'
		assert json.loads(rv.data.decode('utf-8'))['result'] == error_msg

	@pytest.mark.parametrize("dist_matrix_type, test_json, expect_status_code",
		[(0, {"user_id": 1, "num_user": 3}, 200),
		(1, {"user_id": '1234', "num_user": 3}, 200),
		(2, {"user_id": '1234', "num_user": 3}, 500),
		(3, {"user_id": 'test@testEmail.com', "num_user": 3}, 200),
		(4, {"user_id": 'haha', "num_user": 4}, 400)])
	def test_dist_matrix_with_params(self, client, dist_matrix_type, test_json, expect_status_code):
		rv = client.post('/api/distance_matrix/{typ}'.format(typ=dist_matrix_type),
			json=json.dumps(test_json))
		# print ("rv.data = " + str(json.loads(rv.data.decode('utf-8'))))
		assert rv.status_code == expect_status_code

