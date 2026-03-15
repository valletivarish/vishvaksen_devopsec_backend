# EC2 instance configuration for hosting the Spring Boot backend application
# Uses t2.micro (free tier eligible) with Amazon Linux 2023

# Security group controlling inbound and outbound traffic for EC2
resource "aws_security_group" "ec2_sg" {
  name        = "${var.project_name}-ec2-sg"
  description = "Security group for backend EC2 instance"
  vpc_id      = aws_vpc.main.id

  # Allow SSH access for deployment and management
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "SSH access"
  }

  # Allow HTTP traffic on port 8080 for the Spring Boot application
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Spring Boot application port"
  }

  # Allow all outbound traffic
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow all outbound traffic"
  }

  tags = {
    Name    = "${var.project_name}-ec2-sg"
    Project = var.project_name
  }
}

# EC2 instance running the backend application
resource "aws_instance" "backend" {
  ami                    = var.ami_id
  instance_type          = var.instance_type
  subnet_id              = aws_subnet.public.id
  vpc_security_group_ids = [aws_security_group.ec2_sg.id]
  key_name               = var.key_pair_name

  # User data script to install Java and set up systemd service on first boot
  user_data = <<-EOF
              #!/bin/bash
              # Update system packages
              sudo yum update -y
              # Install Java 17 runtime
              sudo yum install -y java-17-amazon-corretto
              # Create application directory
              sudo mkdir -p /opt/inventory-backend
              # Create systemd service file for the Spring Boot application
              cat <<'SERVICE' | sudo tee /etc/systemd/system/inventory-backend.service
              [Unit]
              Description=Inventory Management Backend
              After=network.target
              [Service]
              Type=simple
              User=ec2-user
              ExecStart=/usr/bin/java -jar /opt/inventory-backend/app.jar --spring.profiles.active=prod
              Restart=on-failure
              RestartSec=10
              [Install]
              WantedBy=multi-user.target
              SERVICE
              # Enable the service to start on boot
              sudo systemctl daemon-reload
              sudo systemctl enable inventory-backend
              EOF

  tags = {
    Name    = "${var.project_name}-backend"
    Project = var.project_name
  }
}
