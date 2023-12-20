from flask import Flask, request, jsonify
import joblib
import requests
from datetime import datetime
import pytz

app = Flask(__name__)

# Load the model outside the route
model = joblib.load('modeliot1.pkl')
target_url = 'https://script.google.com/macros/s/AKfycbzu0Nktmdfn2c08tIx_eo42r-E37i6aeHiEcIJGUhVy_N3CrcNs3tZ1bvJlhIrEbfT-oA/exec'

def get_jakarta_datetime():
    jakarta_timezone = pytz.timezone('Asia/Jakarta')
    jakarta_time = datetime.now(jakarta_timezone)
    return jakarta_time.strftime('%Y-%m-%d %H:%M:%S')

@app.route('/predict', methods=['POST'])
def predict():
    try:
        # Get data from the request JSON
        data = request.get_json()
        # Tentukan zona waktu Jakarta
        jakarta_datetime = get_jakarta_datetime()
        # Extract features (temp, ppm, hum) from the request data
        temp = float(data['temp'])
        ppm = int(data['ppm'])
        hum = float(data['hum'])

        # Perform prediction using the loaded model
        prediction = model.predict([[hum, temp, ppm]])

        # Convert prediction to a serializable type (e.g., int, float, str)
        serialized_prediction = int(prediction[0])
        if serialized_prediction == 0:
            category = 'Great'
        elif serialized_prediction == 1:
            category = 'Normal'
        elif serialized_prediction == 2:
            category = 'Bad'
        elif serialized_prediction == 2:
            category = 'Terrible'
        else:
            category = 'Undefined'

        payload = {
            'ppm': ppm,
            'humidity': hum,
            'temperature': temp,
            'category': category,
            'timestamp': jakarta_datetime
        }

        # Send data to the target URL
        response = requests.get(target_url, params=payload)

        # You can customize the response based on your model output
        result = {'prediction': category, 'response_status': response.status_code}

        return result

    except Exception as e:
        return jsonify({'error': str(e)})

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=3000, debug=True)
