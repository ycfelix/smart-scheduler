from flask import render_template, Blueprint

main = Blueprint('main', __name__)

@main.route('/')
def index():
	return "Smart Scheduler server"

@main.route('/help')
def help():
	return render_template('manual.html')