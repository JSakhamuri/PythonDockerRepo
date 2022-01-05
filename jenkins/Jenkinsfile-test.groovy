pipeline {
    agent any
    environment {
        registry = "921066146026.dkr.ecr.us-east-2.amazonaws.com/docker-image-repo"
    }
   
    stages {
        stage('Cloning Git') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/main']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'bdc6398b-d720-4e2d-98bd-de0661a7c96b', url: 'https://github.com/JSakhamuri/PythonDockerRepo.git']]])     
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
  // Uploading Docker images into AWS ECR
    stage('Pushing to ECR') {
     steps{  
         script {
                sh 'aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 921066146026.dkr.ecr.us-east-2.amazonaws.com'
                sh 'docker push 921066146026.dkr.ecr.us-east-2.amazonaws.com/docker-image-repo:latest'
         }
        }
      }
   
         // Stopping Docker containers for cleaner Docker run
     stage('stop previous containers') {
         steps {
            sh 'docker ps -f name=mypythonContainer -q | xargs --no-run-if-empty docker container stop'
            sh 'docker container ls -a -fname=mypythonContainer -q | xargs -r docker container rm'
         }
       }
      
    stage('Docker Run') {
     steps{
         script {
                sh 'docker run -d -p 8096:5000 --rm --name mypythonContainer 921066146026.dkr.ecr.us-east-2.amazonaws.com/docker-image-repo:latest'
            }
      }
    }
    }
}
