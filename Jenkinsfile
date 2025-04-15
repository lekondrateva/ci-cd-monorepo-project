pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        disableConcurrentBuilds()
        durabilityHint(org.jenkinsci.plugins.workflow.flow.FlowDurabilityHint.PERFORMANCE_OPTIMIZED)
    }

    environment {
        APP_CONTAINER_NAME = 'my-app-container'
        APP_IMAGE = 'myapp:latest'
        NETWORK = 'jenkins-net'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout([$class: 'GitSCM', userRemoteConfigs: [[url: 'https://github.com/lekondrateva/ci-cd-monorepo-project.git']]])
            }
        }

        stage('Build Application') {
            steps {
                dir('app') {
                    sh '''
                        docker run --rm \
                          -v "$PWD":/app \
                          -w /app \
                          maven:3.9.6-eclipse-temurin-17 \
                          mvn clean package -DskipTests
                    '''
                }
            }
        }

        stage('Docker Build & Run') {
            steps {
                dir('app') {
                    sh '''
                        docker build -t $APP_IMAGE .
                        docker run -d --name $APP_CONTAINER_NAME --network $NETWORK -p 8080:8080 $APP_IMAGE
                    '''
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
                          if curl -s http://$APP_CONTAINER_NAME:8080/actuator/health | grep -q UP; then
                            echo "App is up!"
                            break
                          fi
                          echo "Still waiting..."
                          sleep 3
                        done
                    '''

                    sh '''
                        docker run --rm \
                          --network $NETWORK \
                          -v "$PWD":/tests \
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
            sh '''
                docker stop $APP_CONTAINER_NAME || true
                docker rm $APP_CONTAINER_NAME || true
            '''
        }
    }
}
