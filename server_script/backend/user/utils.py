# from itsdangerous import TimedJSONWebSignatureSerializer as Serializer
# from flask import app, redirect, url_for
# from backend.api.routes import sql_post

# def get_reset_token(expires_sec=120, user_id):
# 	s = Serializer(app.config['FYPSERVER_SECRET_KEY'], expires_sec)
# 	return s.dumps({'user_id': user_id}).decode('utf-8')

# def verify_reset(token):
# 	s = Serializer(app.config['FYPSERVER_SECRET_KEY'])
# 	try:
# 		user_id = s.loads(token)['user_id']
# 	except:
# 		return None

# 	get_ac_json = {}
# 	get_ac_json['db_name'] = 'Smart Scheduler'
# 	get_ac_json['sql_cmd'] = "select * from Accounts where UserID='{ID}'".format(ID=user_id)
# 	return redirect(url_for(sql_post(inputJSON=get_ac_json)), code=307)

# def verify_user(email):
	
