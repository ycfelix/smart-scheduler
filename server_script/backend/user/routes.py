from flask import jsonify, Blueprint, redirect, url_for, abort
from backend.api.utils import getJSON, getCnxn, getCursor
from backend.user.utils import *
import pyodbc

user = Blueprint('user', __name__)
bad_request_code = 400

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

@user.route('/api/user/reset_password', methods=['GET', 'POST'])
def reset_request():
	pass

@user.route('/api/user/reset_password/<token>', methods=['GET', 'POST'])
def reset_password(token):
	pass

@user.route('/api/user/register', methods=['POST'])
def register():
	json = getJSON()
	return_dict = {}
	return_code = 200
	# ignore mobile phone number first
	if not "email" in json:
		return jsonify({'error_msg': 'No email'}), bad_request_code
	elif not "hashed_pwd" in json:
		return jsonify({'error_msg': 'No password'}), bad_request_code
	elif not "ID" in json:
		return jsonify({'error_msg': 'No ID'}), bad_request_code
	elif json["email"] == "":
	    return jsonify({'error_msg': 'Email is not specified'}), bad_request_code
	elif json["hashed_pwd"] == "":
	    return jsonify({'error_msg': 'Password is not specified'}), bad_request_code
	elif json["ID"] == "":
	    return jsonify({'error_msg': 'ID is not specified'}), bad_request_code
	else:
		try:
			cnxn = getCnxn('Smart Scheduler') # for deployment
			# cnxn = getCnxn('TestData') # for testing
			cursor = cnxn.cursor()
			cmd = "select Email, HashedPwd from dbo.Accounts where Email='%s'" % json['email'] # for deployment
			# cmd = "select Email, HashedPwd from TestAccounts where Email='%s'" % json['email'] # for testing
			check = getCursor(cursor, cmd).fetchall()
			if len(check) == 0:
				cmd = """
				insert into dbo.Accounts values ('{ID}', '{email}', '{pwd}')
				""".format(ID=json['ID'], email=json['email'], pwd=json['hashed_pwd']) # for deployment
				# cmd = """
				# insert into TestAccounts values ('{ID}', '{email}', '{pwd}')
				# """.format(ID=json['ID'], email=json['email'], pwd=json['hashed_pwd']) # for testing
				getCursor(cursor, cmd)
				return_dict['result'] = True
				return_dict['error_code'] = -1 # no error
				return_dict['error_msg'] = 'no error'

			else:
				return_dict['result'] = False
				# return_code = bad_request_code
				if len(check) == 1:
					return_dict['error_code'] = 2 # email already registered
					return_dict['error_msg'] = 'email already registered'
				elif len(check) > 1:
					return_dict['error_code'] = 3 # need debug: check if duplicated email exists in database.
					return_dict['error_msg'] = '[DEBUG] duplicated email in server'

			cnxn.commit()
			cursor.close()
			cnxn.close()
			return jsonify(return_dict)

		except pyodbc.Error as pex:
			abort(500, {'error_msg': 'Database connection error (500).', 'system_msg': str(pex)})

		except Exception as e:
			return_dict['result'] = False
			return_dict['error_code'] = 500 # error
			return_dict['error_msg'] = str(e)
			return return_dict

@user.route('/api/user/login', methods=['POST'])
def login():
	json = getJSON()
	return_dict = {}
	return_code = 200
	if not "email" in json:
		return jsonify({'error_msg': 'No email'}), bad_request_code
	elif not "hashed_pwd" in json:
		return jsonify({'error_msg': 'No password'}), bad_request_code
	elif json["email"] == "":
	    return jsonify({'error_msg': 'Email is not specified'}), bad_request_code
	elif json["hashed_pwd"] == "":
	    return jsonify({'error_msg': 'Password is not specified'}), bad_request_code
	else:
		try:
			cnxn = getCnxn('Smart Scheduler') # for deployment
			# cnxn = getCnxn('TestData') # for testing
			cursor = cnxn.cursor()

			cmd = "select Email, HashedPwd from dbo.Accounts where Email='%s'" % json['email'] # for deployment
			# cmd = "select Email, HashedPwd from TestAccounts where Email='%s'" % json['email'] # for testing
			check = getCursor(cursor, cmd).fetchall()
			# print ("len(check) = " + str(len(check)))
			if len(check) == 1:
				# password checking can be improved -> hashing
				# print ("match password?" + str(json['hashed_pwd'] == check[0][1]))
				if (json['hashed_pwd'] == check[0][1]):
					# getCursor(cursor, "update dbo.Accounts set LogIn=1 where Email='%s'" % json['email'])
					return_dict['result'] = True
					return_dict['error_code'] = -1 # no error
					return_dict['error_msg'] = 'no error'
				else:
					return_dict['result'] = False
					return_dict['error_code'] = 1 # wrong password
					return_dict['error_msg'] = 'wrong password'
					# return_code = bad_request_code

			else:
				return_dict['result'] = False
				# return_code = bad_request_code
				if len(check) == 0:
					return_dict['error_code'] = 2 # email not registered
					return_dict['error_msg'] = 'email not registered'
				elif len(check) > 1:
					return_dict['error_code'] = 3 # need debug: check if duplicated email exists in database.
					return_dict['error_msg'] = '[DEBUG] duplicated email in server'

			cnxn.commit()
			cursor.close()
			cnxn.close()
			return jsonify(return_dict)

		except pyodbc.Error as pex:
			abort(500, {'error_msg': 'Please see system message to see what is wrong.', 'system_msg': str(pex)})

		except Exception as e:
			return_dict['result'] = False
			return_dict['error_code'] = 500 # error
			return_dict['error_msg'] = str(e)
			return return_dict