import numpy as np
import tensorflow as tf
from PIL import Image

# Load the TFLite model
interpreter = tf.lite.Interpreter(model_path="efficientnet.tflite")
interpreter.allocate_tensors()

# Get input and output tensor details
input_details = interpreter.get_input_details()
output_details = interpreter.get_output_details()

# Load and preprocess the image
img_path = "mal_test.png"
img = Image.open(img_path).convert('RGB')

# Resize the image to the input size of EfficientNet (e.g., 224x224 for EfficientNetB0)
img = img.resize((224, 224))

# Convert image to a numpy array
img_array = np.array(img)

# Normalize the image (EfficientNet models often require normalization to [0, 1] or [-1, 1])
img_array = img_array / 255.0  # Normalize to [0, 1]

# Add batch dimension (1, 224, 224, 3)
img_array = np.expand_dims(img_array, axis=0)

# Ensure the image is of the correct type (float32)
img_array = img_array.astype(np.float32)

# Set the input tensor
interpreter.set_tensor(input_details[0]['index'], img_array)

# Run inference
interpreter.invoke()

# Get the output tensor
output_data = interpreter.get_tensor(output_details[0]['index'])

print("Prediction:", output_data)
print("")
# Confidence class labels
class_labels = ['benign', 'malignant', 'normal']

# Display the prediction confidence for each class
prediction_confidence = output_data[0]  # Extract the confidence values for the image

# Print the confidence values and their corresponding class labels
print("Prediction Confidence Intervals:")
print("")
for i, label in enumerate(class_labels):
    print(f"{label}: {prediction_confidence[i] * 100:.2f}%")

# Optionally, show the predicted class with the highest confidence
predicted_class_index = np.argmax(prediction_confidence)
predicted_class = class_labels[predicted_class_index]
print(f"\nPredicted Class: {predicted_class} with {prediction_confidence[predicted_class_index] * 100:.2f}% confidence.")