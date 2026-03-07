# Terraform output values displayed after infrastructure provisioning
# Provides URLs and connection details for the deployed application

output "backend_public_ip" {
  description = "Public IP address of the EC2 backend server"
  value       = aws_instance.backend.public_ip
}

output "backend_api_url" {
  description = "URL for accessing the backend API"
  value       = "http://${aws_instance.backend.public_ip}:8080"
}

output "rds_endpoint" {
  description = "RDS PostgreSQL connection endpoint"
  value       = aws_db_instance.main.endpoint
}

output "rds_database_name" {
  description = "Name of the PostgreSQL database"
  value       = aws_db_instance.main.db_name
}

output "frontend_website_url" {
  description = "S3 static website URL for the React frontend"
  value       = aws_s3_bucket_website_configuration.frontend.website_endpoint
}

output "s3_bucket_name" {
  description = "S3 bucket name for frontend deployment"
  value       = aws_s3_bucket.frontend.id
}
