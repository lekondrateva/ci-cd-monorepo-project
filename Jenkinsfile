pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Application') {
            steps {
                dir('app') {
                    script {
                        docker.image('maven:3.9.6-eclipse-temurin-17').inside {
                            sh 'mvn clean package -DskipTests'
                        }
                    }
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
                sleep(time: 20, unit: "SECONDS")
                dir('tests') {
                    script {
                        docker.image('maven:3.9.6-eclipse-temurin-17').inside {
                            sh 'mvn test'
                        }
                    }
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
