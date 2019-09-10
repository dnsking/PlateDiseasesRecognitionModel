import pickle
import json
import time
import io
from PIL import Image
from keras.models import load_model
from keras.preprocessing.sequence import pad_sequences
from azureml.core.model import Model
import base64
from keras.preprocessing import image
import numpy as np
import tensorflow as tf


def init():
    global model
    global output_list
    global graph
    model_path = Model.get_model_path('plantdiseases')
    graph = tf.get_default_graph()
    model = load_model( model_path+'/AlexNetModel.hdf5')
    output_dict = {'Apple___Apple_scab': 0,
               'Apple___Black_rot': 1,
               'Apple___Cedar_apple_rust': 2,
               'Apple___healthy': 3,
               'Blueberry___healthy': 4,
               'Cherry_(including_sour)___Powdery_mildew': 5,
               'Cherry_(including_sour)___healthy': 6,
               'Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot': 7,
               'Corn_(maize)___Common_rust_': 8,
               'Corn_(maize)___Northern_Leaf_Blight': 9,
               'Corn_(maize)___healthy': 10,
               'Grape___Black_rot': 11,
               'Grape___Esca_(Black_Measles)': 12,
               'Grape___Leaf_blight_(Isariopsis_Leaf_Spot)': 13,
               'Grape___healthy': 14,
               'Orange___Haunglongbing_(Citrus_greening)': 15,
               'Peach___Bacterial_spot': 16,
               'Peach___healthy': 17,
               'Pepper,_bell___Bacterial_spot': 18,
               'Pepper,_bell___healthy': 19,
               'Potato___Early_blight': 20,
               'Potato___Late_blight': 21,
               'Potato___healthy': 22,
               'Raspberry___healthy': 23,
               'Soybean___healthy': 24,
               'Squash___Powdery_mildew': 25,
               'Strawberry___Leaf_scorch': 26,
               'Strawberry___healthy': 27,
               'Tomato___Bacterial_spot': 28,
               'Tomato___Early_blight': 29,
               'Tomato___Late_blight': 30,
               'Tomato___Leaf_Mold': 31,
               'Tomato___Septoria_leaf_spot': 32,
               'Tomato___Spider_mites Two-spotted_spider_mite': 33,
               'Tomato___Target_Spot': 34,
               'Tomato___Tomato_Yellow_Leaf_Curl_Virus': 35,
               'Tomato___Tomato_mosaic_virus': 36,
               'Tomato___healthy': 37}

    output_list = list(output_dict.keys())
def run(data):
    try:
        start_at = time.time()
        # Pick out the text property of the JSON request.
        # This expects a request in the form of {"text": "some text to score for sentiment"}
        #data = json.loads(data)
        prediction,prob = predict(data)
        #Return prediction
        return {"label": prediction, "probability":str(prob),
       "elapsed_time": time.time()-start_at} 
    except Exception as e:
        error = str(e)
        print("error "+error)
        return "error "+error

def predict(encoded):
    decoded = base64.b64decode(encoded)
    myimage = Image.open(io.BytesIO(decoded))
    img = myimage.resize( (224, 224))
    img = image.img_to_array(img)
    img = np.expand_dims(img, axis=0)
    img = img/255
    with graph.as_default():
        prediction = model.predict(img)

    prediction_flatten = prediction.flatten()
    max_val_index = np.argmax(prediction_flatten)
    result = output_list[max_val_index]
    #print(str(result)+" prob "+str(prediction_flatten[max_val_index]))
    prob = prediction_flatten[max_val_index]
    return result,prob

#with open('Gray-leaf-spot-Adam-Sisson-3-copy.jpg', 'rb') as imgFile:
#    pic = base64.b64encode(imgFile.read())
#init()
#run(pic)