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
  
    // Create ECR Repository
    stage('Create Repository') {
      steps{
        script {
          'aws ecr create-repository --repository-name docker-image-repo --image-scanning-configuration scanOnPush=true'
        }
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
                'aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 921066146026.dkr.ecr.us-east-2.amazonaws.com'
                'docker push 921066146026.dkr.ecr.us-east-2.amazonaws.com/docker-image-repo:latest'
         }
        }
      }
   // Get the scan results
    stage('Get Scan Results') {
     steps{  
         script {
                'aws ecr describe-image-scan-findings --repository-name docker-image-repo --image-id imageTag=latest --region us-east-2'
         }
        }
      }
  
    }
}
