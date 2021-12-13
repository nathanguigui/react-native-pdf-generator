
Pod::Spec.new do |s|
  s.name         = "RNReactNativePdfGenerator"
  s.version      = "1.0.0"
  s.summary      = "RNReactNativePdfGenerator"
  s.description  = <<-DESC
                  RNReactNativePdfGenerator
                   DESC
  s.homepage     = "https://github.com/nathanguigui/react-native-pdf-generator.git"
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/nathanguigui/react-native-pdf-generator.git", :tag => "main" }
  s.source_files  = "RNReactNativePdfGenerator/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

  
