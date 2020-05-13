import json, pytest
from backend.api.utils import getCnxn

cnxn = None
cursor = None
cmd = None

def setup_module():
    """ setup any state specific to the execution of the given module."""
    print()
    print('--- setup in module level, test_user.py ---')
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

# REMEMBER to uncomment appropriate lines in backend/user/routes.py for testing.

def teardown_module():
    """ teardown any state that was previously setup with a setup_module
    method.
    """
    print()
    print('--- teardown in module level, test_user.py ---')
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

class TestRegister:

	@pytest.mark.parametrize("test_json, error_code",
		[({}, 400), 
		({"email": ""}, 400),
		({"hashed_pwd": ""}, 400),
		({"ID": ""}, 400),
		({"email": "", "hashed_pwd":""}, 400),
		({"ID": "", "hashed_pwd":""}, 400),
		({"ID": "", "email":""}, 400),
		({"email": "123@456.com", "hashed_pwd": "", "ID": ""}, 400),
		({"email": "", "hashed_pwd": "", "ID": "this_is_a_user_id"}, 400),
		({"email": "", "hashed_pwd": "", "ID": "9ev4b"}, 400),
		({"email": "123@456.com", "hashed_pwd": "brrruuuhhh", "ID": ""}, 400),
		({"email": "123@456.com", "hashed_pwd": "", "ID": "4beaw"}, 400),
		({"email": "", "hashed_pwd": "brrruuuhhh", "ID": "4beaw"}, 400)])
	def test_register_check_miss_param(self, client, test_json, error_code):
		rv = client.post('/api/user/register', json=json.dumps(test_json))
		assert rv.status_code == error_code

	@pytest.mark.parametrize("reg_json, error_code",
		[
		({"email": "test@testEmail.com", "hashed_pwd": "haha", "ID": "1e2d6"}, -1),
		({"email": "test@testEmail.com", "hashed_pwd": "haha", "ID": "1e2d6"}, 2),
		({"email": "test@testing.com", "hashed_pwd": "haha", "ID": "43fe2"}, -1),
		({"email": "WorldWarIII@doodleMail.com", "hashed_pwd": "testing12345", "ID": "721831"}, 2),
		({"email": "test@dummyEmail.com", "hashed_pwd": "hahayoyo!", "ID": "1234"}, 2),
		({"email": "test2@dummyEmail.com", "hashed_pwd": "hehehaha~", "ID": "2345"}, 2),
		({"email": "test2@dummyEmail.com", "hashed_pwd": "hello_world!!!", "ID": "eq94k"}, 2)
		])
	def test_register_different_params(self, client, reg_json, error_code):
		rv = client.post('api/user/register', json=json.dumps(reg_json))
		assert json.loads(rv.data.decode('utf-8'))['error_code'] == error_code

class TestLogin:
	
	@pytest.mark.parametrize("test_json, error_code",
		[({}, 400), 
		({"email": ""}, 400),
		({"hashed_pwd": ""}, 400),
		({"email": "", "hashed_pwd":""}, 400),
		({"email": "123@456.com", "hashed_pwd": ""}, 400),
		({"email": "", "hashed_pwd": "heheahhahohoyyo!!!!"}, 400)])
	def test_login_check_miss_param(self, client, test_json, error_code):
		rv = client.post('/api/user/login', json=json.dumps(test_json))
		# print (json.loads(rv.data.decode('utf-8'))['error_msg'])
		assert rv.status_code == error_code

	@pytest.mark.parametrize("login_json, error_code",
		[
		({"email": "test@testEmail.com", "hashed_pwd": "haha"}, -1),
		({"email": "test@testing.com", "hashed_pwd": "haha"}, -1),
		({"email": "WorldWarIII@doodleMail.com", "hashed_pwd": "testing12345"}, -1),
		({"email": "test@dummyEmail.com", "hashed_pwd": "hahayoyo!"}, -1),
		({"email": "test2@dummyEmail.com", "hashed_pwd": "hehehaha~"}, -1),
		({"email": "test2@dummyEmail.com", "hashed_pwd": "hehehaha_oops!"}, 1),
		({"email": "WorldWarIII@doodleMail.com", "hashed_pwd": "testing1234"}, 1),
		({"email": "test@dummyEmail.com", "hashed_pwd": "!yoyohaha"}, 1),
		({"email": "test@gmail.com", "hashed_pwd": "!yoyohaha"}, 2)
		])
	def test_login_different_params(self, client, login_json, error_code):
		rv = client.post('api/user/login', json=json.dumps(login_json))
		# print (json.loads(rv.data.decode('utf-8'))['error_msg'])
		assert json.loads(rv.data.decode('utf-8'))['error_code'] == error_code