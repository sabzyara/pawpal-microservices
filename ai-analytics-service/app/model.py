from sklearn.linear_model import LogisticRegression
import numpy as np

class HealthModel:
    def __init__(self):
        self.model = LogisticRegression()
        X = np.array([[1,2], [5,1], [10,0], [2,5]])
        y = np.array([0, 1, 1, 0])
        self.model.fit(X, y)

    def predict(self, age, activity):
        return self.model.predict([[age, activity]])[0]
