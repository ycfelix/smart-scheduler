from flask import jsonify, Blueprint, redirect, url_for
from backend.api.utils import *

user = Blueprint('user', __name__)

# @user.route('/api/user/logout', methods=['POST'])
# def logout():
# 	pass
	# json = getJSON()
	# if not "email" in json:
	# 	return jsonify({'error': 'No email'})
	# elif "email" == "":
	#     return jsonify({'error': 'Email is not specified'})
	# else:
	# 	try:
	# 		cnxn = getCnxn('Smart Scheduler')
	# 		cursor = cnxn.cursor()

	# 	except pyodbc.Error as pex:
	# 		return jsonify({'error': 500, 'error msg': str(pex)})

	# 	else:
	# 		# cmd = ""
	# 		return_dict['result'] = 'To be constructed'
	# 		return jsonify(return_dict)

@user.route('/api/user/register', methods=['POST'])
def register():
	json = getJSON()
	return_dict = {}
	# ignore mobile phone number first
	if not "email" in json:
		return jsonify({'error': 'No email'})
	elif not "hashed_pwd" in json:
		return jsonify({'error': 'No password'})
	elif not "ID" in json:
		return jsonify({'error': 'No ID'})
	elif "email" == "":
	    return jsonify({'error': 'Email is not specified'})
	elif "hashed_pwd" == "":
	    return jsonify({'error': 'Password is not specified'})
	elif "ID" == "":
	    return jsonify({'error': 'ID is not specified'})
	else:
		try:
			cnxn = getCnxn('Smart Scheduler')
			cursor = cnxn.cursor()

		except pyodbc.Error as pex:
			return jsonify({'error_code': 500, 'error msg': str(pex)})

		else:
			# return_dict = {}
			cmd = "select Email, HashedPwd from dbo.Accounts where Email='%s'" % json['email']
			check = getCursor(cursor, cmd).fetchall()
			if len(check) == 0:
				# password checking can be improved -> hashing
				try:
					# print ("Hashed password = " + json['hashed_pwd'])
					# cmd = "insert into dbo.Accounts values ({ID}, '{email}', '{pwd}', {login})"\
					# .format(ID=json['ID'], email=json['email'], pwd=json['hashed_pwd'], login=0)
					if (json['hashed_pwd'] == "'"):
						json['hashed_pwd'] = "''"
					cmd = """
					insert into dbo.Accounts values ('{ID}', '{email}', '{pwd}')
					""".format(ID=json['ID'], email=json['email'], pwd=json['hashed_pwd'])
					getCursor(cursor, cmd)
					return_dict['result'] = True
					return_dict['error_code'] = -1 # no error
					return_dict['error_msg'] = 'no error'

				except Exception as e:
					return_dict['result'] = False
					return_dict['error_code'] = 1 # error
					return_dict['error_msg'] = str(e)

			else:
				return_dict['result'] = False
				if len(check) == 1:
					return_dict['error_code'] = 2 # email already registered
					return_dict['error_msg'] = 'email already registered'
				elif len(check) > 1:
					return_dict['error_code'] = 3 # need debug: check if duplicated email exists in database.
					return_dict['error_msg'] = '[DEBUG] duplicated email in server'
				else:
					return_dict['error_code'] = 4 # server error
					return_dict['error_msg'] = 'server error'

		cnxn.commit()
		cursor.close()
		cnxn.close()
		return jsonify(return_dict)

@user.route('/api/user/login', methods=['POST'])
def login():
	json = getJSON()
	return_dict = {}
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
			# return_dict = {}
			cmd = "select Email, HashedPwd from dbo.Accounts where Email='%s'" % json['email']
			check = getCursor(cursor, cmd).fetchall()
			if len(check) == 1:
				# password checking can be improved -> hashing
				if (json['hashed_pwd'] == check[0][1]):
					# getCursor(cursor, "update dbo.Accounts set LogIn=1 where Email='%s'" % json['email'])
					return_dict['result'] = True
					return_dict['error_code'] = -1 # no error
					return_dict['error_msg'] = 'no error'
				else:
					return_dict['result'] = False
					return_dict['error_code'] = 1 # wrong password
					return_dict['error_msg'] = 'wrong password'

			else:
				return_dict['result'] = False
				if len(check) == 0:
					return_dict['error_code'] = 2 # email not registered
					return_dict['error_msg'] = 'email not registered'
				elif len(check) > 1:
					return_dict['error_code'] = 3 # need debug: check if duplicated email exists in database.
					return_dict['error_msg'] = '[DEBUG] duplicated email in server'
				else:
					return_dict['error_code'] = 4 # server error
					return_dict['error_msg'] = 'server error'

		cnxn.commit()
		cursor.close()
		cnxn.close()
		return jsonify(return_dict)