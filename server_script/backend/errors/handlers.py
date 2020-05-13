from flask import Blueprint, jsonify

errors = Blueprint('errors', __name__)

@errors.app_errorhandler(404)
def error_404(error):
	return jsonify({'error_msg': 'Resource not found (404)!'}), 404

@errors.app_errorhandler(403)
def error_403(error):
	return jsonify({'error_msg': 'Restricted resources (403)!'}), 403

@errors.app_errorhandler(500)
def error_500(error):
	return jsonify({'error': 'Server error (500)!',\
	        'error_msg': error.description['error_msg'], \
	        'system_msg': error.description['system_msg']}), 500

@errors.app_errorhandler(400)
def error_400(error):
	return jsonify({'error_msg': 'Bad request! (400)!'}), 400
