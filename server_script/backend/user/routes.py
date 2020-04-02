from flask import jsonify, Blueprint
from backend.api.utils import getJSON, getCnxn, getCursor
import pyodbc

user = Blueprint('user', __name__)

@user.route('/api/user/logout', methods=['GET'])
def logout():
	json = getJSON()
	pass

@user.route('/api/user/login', methods=['POST'])
def login():
	json = getJSON();
	if not "email" in json:
		return jsonify({'error': 'No email'})
	elif not "hashed_pwd" in json:
		return jsonify({'error': 'No password'})
	elif "email" == "":
	    return jsonify({'error': 'Email is not specified'})
	elif "hashed_pwd" == "":
	    return jsonify({'error': 'Password is not specified'})
	else:
		try:
			cnxn = getCnxn('Smart Scheduler')
			cursor = cnxn.cursor()

		except pyodbc.Error as pex:
			return jsonify({'error': 500, 'error msg': str(pex)})

		else:
			return_dict = {}
			str = "select Email, HashedPwd, LogIn from dbo.Accounts where Email='%s'" % json['email']
			check = getCursor(cursor, str).fetchall()
			if len(check) == 1:
				# password checking can be improved -> hashing
				if (json['hashed_pwd'] == check[0][1]):
					if (check[0][2] == 0):
						getCursor(cursor, "update dbo.Accounts set LogIn=1 where Email='%s'" % json['email'])
						return_dict['result'] = True
						return_dict['error_code'] = -1 # no error
					else:
						return_dict['result'] = False
						return_dict['error_code'] = 0 # already logged in
				else:
					return_dict['result'] = False
					return_dict['error_code'] = 1 # wrong password

			else:
				return_dict['result'] = False
				if len(check) == 0:
					return_dict['error_code'] = 2 # email not registered
				elif len(check) > 1:
					return_dict['error_code'] = 3 # need debug: check if duplicated email exists in database.
				else:
					return_dict['error_code'] = 4 # server error

		cnxn.commit()
		cursor.close()
		cnxn.close()
		return jsonify(return_dict)