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
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Docker Build & Run') {
            steps {
                dir('app') {
                    sh 'docker build -t my-app:latest .'
                    sh 'docker run -d --name my-app-container -p 8080:8080 my-app:latest'
                }
            }
        }

        stage('Run Tests') {
            steps {
                sleep(time:10, unit:"SECONDS") // ожидание запуска приложения
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
