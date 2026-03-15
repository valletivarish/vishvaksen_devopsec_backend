# Output values for the Inventory Management System infrastructure
# These outputs display important URLs and connection information after deployment

output "ec2_public_ip" {
  description = "Public IP address of the backend EC2 instance"
  value       = aws_instance.backend.public_ip
}

output "ec2_public_dns" {
  description = "Public DNS name of the backend EC2 instance"
  value       = aws_instance.backend.public_dns
}

output "backend_url" {
  description = "URL to access the backend application"
  value       = "http://${aws_instance.backend.public_ip}:8080"
}

output "rds_endpoint" {
  description = "RDS MySQL database endpoint for application configuration"
  value       = aws_db_instance.mysql.endpoint
}

output "s3_bucket_url" {
  description = "S3 bucket website URL for static assets"
  value       = aws_s3_bucket_website_configuration.app_bucket.website_endpoint
}

output "swagger_ui_url" {
  description = "Swagger UI URL for API documentation"
  value       = "http://${aws_instance.backend.public_ip}:8080/swagger-ui.html"
}
