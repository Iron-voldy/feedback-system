<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>RhythmWave - Sign Up</title>
  <!-- Bootstrap CSS -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
  <!-- Font Awesome Icons -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/signup.css">
  <style>

  </style>
</head>
<body>
<!-- Auth Container -->
<div class="auth-container">
  <div class="auth-card">
    <div class="auth-header">
      <div class="auth-icon">
        <i class="fas fa-music"></i>
      </div>
      <h2>Create Your RhythmWave Account</h2>
      <p class="text-secondary">Join our community of music lovers today</p>
    </div>

    <form id="signupForm" action="${pageContext.request.contextPath}/RegisterServlet" method="post">
      <div class="form-group">
        <label for="first-name" class="form-label">First Name</label>
        <input type="text" class="form-control" id="first-name" name="first-name" placeholder="Enter your first name" required>
      </div>

      <div class="form-group">
        <label for="last-name" class="form-label">Last Name</label>
        <input type="text" class="form-control" id="last-name" name="last-name" placeholder="Enter your last name" required>
      </div>

      <div class="form-group">
        <label for="email" class="form-label">Email address</label>
        <input type="email" class="form-control" id="email" name="email" placeholder="name@example.com" required>
      </div>

      <div class="form-group password-input-group">
        <label for="password" class="form-label">Password</label>
        <input type="password" class="form-control" id="password" name="password" placeholder="Create a strong password" required>
        <span class="password-toggle" id="signup-password-toggle">
          <i class="far fa-eye"></i>
        </span>
      </div>

      <div class="form-group password-input-group">
        <label for="signup-confirm-password" class="form-label">Confirm Password</label>
        <input type="password" class="form-control" id="signup-confirm-password" placeholder="Confirm your password" required>
        <span class="password-toggle" id="signup-confirm-password-toggle">
          <i class="far fa-eye"></i>
        </span>
      </div>

      <div class="form-group">
        <label for="signup-genre" class="form-label">Favorite Music Genre</label>
        <select class="form-control" id="signup-genre" name="signup-genre">
          <option value="">Select your favorite genre</option>
          <option value="rock">Rock</option>
          <option value="pop">Pop</option>
          <option value="jazz">Jazz</option>
          <option value="hiphop">Hip Hop</option>
          <option value="electronic">Electronic</option>
          <option value="classical">Classical</option>
          <option value="country">Country</option>
          <option value="r&b">R&B</option>
        </select>
      </div>

      <button type="submit" class="btn btn-primary btn-lg">Create Account</button>

      <div class="text-center mt-4">
        <p class="text-secondary">Already have an account? <a href="login.jsp" class="text-primary">Log In</a></p>
      </div>
    </form>
  </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>