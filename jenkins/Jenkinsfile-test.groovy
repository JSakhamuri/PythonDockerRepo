pipeline {
    agent any
    environment {
        registry = "921066146026.dkr.ecr.us-east-2.amazonaws.com/docker-image-repo"
    }
   
    stages {
        stage('Cloning Git') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: 'origin/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '7228fe90-2df5-4e7e-9cdc-f64967eec2dd', url: 'https://github.com/JSakhamuri/PythonDockerRepo.git']]])     
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