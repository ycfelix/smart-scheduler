# app __init__
from flask import Flask
# from flask_mail import Mail
from backend.config import Config

# mail = Mail()

def create_app(config_class=Config, test_config=None):
	app = Flask(__name__)
	app.config.from_object(config_class)
	if not test_config is None:
		app.config.update(test_config)
	# mail.init_app(app)
	from backend.api.routes import api
	from backend.main.routes import main
	from backend.user.routes import user
	from backend.errors.handlers import errors
	app.register_blueprint(api)
	app.register_blueprint(main)
	app.register_blueprint(user)
	app.register_blueprint(errors)

	return app