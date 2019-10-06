import flask
import werkzeug
#from flask_ngrok import run_with_ngrok
import os


app = flask.Flask(__name__)
#run_with_ngrok(app)
@app.route('/', methods = ['GET', 'POST'])
def handle_request():
    imagefile = flask.request.files['image']
    filename = werkzeug.utils.secure_filename(imagefile.filename)
    SITE_ROOT = os.path.realpath(os.path.dirname(__file__))
    url = os.path.join(SITE_ROOT+"\static", imagefile.filename)
    print("\nReceived image File name : " + url)
    
    imagefile.save(url)
    
    return "Image Uploaded Successfully and Received Image is."
    #return send_file(url, mimetype='image/gif')
# @app.route('/test', methods = ['GET', 'POST'])
# def handle():
#     return "Image Uploaded Successfully"

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5000, debug=True)