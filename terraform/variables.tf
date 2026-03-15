# Input variables for the Inventory Management System infrastructure
# These variables allow customization of the deployment without modifying main configuration

variable "aws_region" {
  description = "AWS region for deploying all resources"
  type        = string
  default     = "eu-west-1"
}

variable "project_name" {
  description = "Project name used for tagging and naming all AWS resources"
  type        = string
  default     = "inventory-management"
}

variable "instance_type" {
  description = "EC2 instance type for the backend server"
  type        = string
  default     = "t2.micro"
}

variable "ami_id" {
  description = "AMI ID for the EC2 instance (Amazon Linux 2023)"
  type        = string
  default     = "ami-0c38b837cd80f13bb"
}

variable "key_pair_name" {
  description = "Name of the EC2 key pair for SSH access"
  type        = string
  default     = "inventory-key"
}

variable "db_instance_class" {
  description = "RDS instance class for the MySQL database"
  type        = string
  default     = "db.t3.micro"
}

variable "db_username" {
  description = "Master username for the RDS MySQL database"
  type        = string
  default     = "admin"
  sensitive   = true
}

variable "db_password" {
  description = "Master password for the RDS MySQL database"
  type        = string
  sensitive   = true
}
