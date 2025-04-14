pipeline {
    agent {
        docker {
            image 'maven:3.9.6-eclipse-temurin-17'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Application') {
            steps {
                dir('app') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Docker Build & Run') {
            steps {
                dir('app') {
                    sh 'docker build -t myapp:latest .'
                    sh 'docker run -d --name my-app-container -p 8080:8080 myapp:latest'
                }
            }
        }

        stage('Run Tests') {
            steps {
                sleep(time: 10, unit: "SECONDS")
                dir('tests') {
                    sh 'mvn clean test'
                }
            }
            post {
                always {
                    allure([
                        results: [[path: 'tests/target/allure-results']]
                    ])
                }
            }
        }
    }

    post {
        always {
            sh 'docker stop my-app-container || true && docker rm my-app-container || true'
        }
    }
}
