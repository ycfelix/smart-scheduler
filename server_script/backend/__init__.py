from flask import Flask
from backend.config import Config

def create_app(config_class=Config):
	app = Flask(__name__)
	app.config.from_object(Config)

	from backend.api.routes import api
	from backend.main.routes import main
	from backend.user.routes import user
	# from backend.errors.handlers import errors
	app.register_blueprint(api)
	app.register_blueprint(main)
	app.register_blueprint(user)
	# app.register_blueprint(errors)

	return app