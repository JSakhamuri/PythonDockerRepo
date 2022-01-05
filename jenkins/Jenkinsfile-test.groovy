pipeline {
    agent any
    environment {
        registry = "921066146026.dkr.ecr.us-east-2.amazonaws.com/docker-image-repo"
    }
   
    stages {
        stage('Cloning Git') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '7228fe90-2df5-4e7e-9cdc-f64967eec2dd', url: 'https://github.com/JSakhamuri/PythonDockerRepo.git']]])     
            }
        }
  
    // Building Docker images
    stage('Building image') {
      steps{
        script {
          dockerImage = docker.build registry
        }
      }
    }
   
    }
}
