# S3 bucket configuration for storing application artifacts and static assets
# Static website hosting is enabled for potential frontend deployment

resource "aws_s3_bucket" "app_bucket" {
  bucket = "${var.project_name}-assets-${var.aws_region}"

  tags = {
    Name    = "${var.project_name}-assets"
    Project = var.project_name
  }
}

# Enable static website hosting on the S3 bucket
resource "aws_s3_bucket_website_configuration" "app_bucket" {
  bucket = aws_s3_bucket.app_bucket.id

  index_document {
    suffix = "index.html"
  }

  error_document {
    key = "index.html"
  }
}

# Block public access settings - allowing public read for static website hosting
resource "aws_s3_bucket_public_access_block" "app_bucket" {
  bucket = aws_s3_bucket.app_bucket.id

  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

# Bucket policy to allow public read access for static website hosting
resource "aws_s3_bucket_policy" "app_bucket" {
  bucket = aws_s3_bucket.app_bucket.id
  depends_on = [aws_s3_bucket_public_access_block.app_bucket]

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid       = "PublicReadGetObject"
        Effect    = "Allow"
        Principal = "*"
        Action    = "s3:GetObject"
        Resource  = "${aws_s3_bucket.app_bucket.arn}/*"
      }
    ]
  })
}
