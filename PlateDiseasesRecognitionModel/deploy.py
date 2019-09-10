from azureml.core.model import Model
from azureml.core.model import InferenceConfig
from azureml.core import Workspace, Run
from azureml.core.webservice import Webservice,AciWebservice

print("ws = Workspace.from_config")
ws = Workspace.from_config()

print("model = Model.register")
#model = Model.register(model_path = "./models", model_name = "plantdiseases",description = "Plant disease recognition model running on Azure Machine Learning service", workspace = ws)

print("inference_config = InferenceConfig")
inference_config = InferenceConfig(runtime= "python", 
                                   entry_script="PlateDiseasesRecognitionModel.py",
                                   conda_file="env.yml")


print("Model(ws, name='plantdiseases')")
model = Model(ws, name='plantdiseases')



print("deployment_config = AciWebservice")

deployment_config = AciWebservice.deploy_configuration(cpu_cores = 1, memory_gb = 1)

print("service = Model.deploy")
service = Model.deploy(ws, 'somerandomserviceniza2', [model], inference_config, deployment_config)
#print(service.get_logs())

service.wait_for_deployment(True)
print(service.state)
print("scoring URI: " + service.scoring_uri)