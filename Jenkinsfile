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
                dir('.') {
                    script {
                        def workspacePath = pwd()
                        sh """
                            docker run --rm \
                              -v ${workspacePath}:/project \
                              -w /project \
                              maven:3.9.6-eclipse-temurin-17 \
                              mvn clean package -DskipTests
                        """
                    }
                }
            }
        }

        stage('Docker Build & Run') {
            steps {
                dir('app') {
                    sh 'docker build -t myapp:latest .'
                    sh 'docker run -d --name my-app-container --network jenkins-net -p 8080:8080 myapp:latest'
                }
            }
        }

        stage('Run Tests') {
            steps {
                sleep(time: 20, unit: "SECONDS")
                dir('tests') {
                    sh '''
                        echo "Waiting for app to become healthy..."
                        for i in {1..10}; do
                          if curl -s http://my-app-container:8080/actuator/health | grep -q UP; then
                            echo "App is up!"
                            break
                          fi
                          echo "Still waiting..."
                          sleep 3
                        done
                    '''

                    sh '''
                        docker run --rm \
                          --network jenkins-net \
                          -v $(pwd):/tests \
                          -w /tests \
                          maven:3.9.6-eclipse-temurin-17 \
                          mvn test
                    '''
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
