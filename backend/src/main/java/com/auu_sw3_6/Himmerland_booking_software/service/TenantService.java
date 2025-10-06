package com.auu_sw3_6.Himmerland_booking_software.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Booking;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Tenant;
import com.auu_sw3_6.Himmerland_booking_software.api.model.User;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.BookingStatus;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.TenantRepository;
import com.auu_sw3_6.Himmerland_booking_software.exception.RestrictedUsernameException;
import com.auu_sw3_6.Himmerland_booking_software.exception.UserAlreadyExistsException;

import jakarta.annotation.PostConstruct;

@Service
public class TenantService extends UserService<Tenant> {

  private final AdminService adminService;
  private final TenantRepository tenantRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${restricted.usernames}")
  private String restrictedUsernames;

  private Set<String> restrictedUsernamesSet;

  @Autowired
  public TenantService(TenantRepository tenantRepository,
      PictureService profilePictureService,
      PasswordEncoder passwordEncoder,
      AdminService adminService,
      BookingService bookingService) {
    super(tenantRepository, profilePictureService, passwordEncoder, bookingService);
    this.adminService = adminService;
    this.tenantRepository = tenantRepository;
    this.passwordEncoder = passwordEncoder;
  }

  private Set<String> initializeRestrictedUsernames() {
    if (restrictedUsernames == null || restrictedUsernames.isEmpty()) {
      System.out.println("Restricted usernames not set.");
      return new HashSet<>();
    }
    return new HashSet<>(Arrays.asList(restrictedUsernames.split(",")));
  }

  public List<Tenant> getAllTenants() {
    return tenantRepository.findAll().stream()
        .filter(tenant -> !tenant.getName().equals("deleted")).toList();
  }

  public Tenant createTenant(Tenant tenant, MultipartFile profilePicture) {
    if (tenant.getUsername() == null || tenant.getUsername().isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty.");
    }

    if (restrictedUsernamesSet.contains(tenant.getUsername())) {
      throw new RestrictedUsernameException(tenant.getUsername());
    }

    if (userExists(tenant.getUsername())) {
      throw new UserAlreadyExistsException(tenant.getUsername());
    }

    return createUser(tenant, profilePicture);
  }

  private boolean userExists(String username) {
    return getUserByUsername(username).isPresent() || adminService.getUserByUsername(username).isPresent();
  }

  public void softDeleteTenant(Long userId) {
    Optional<Tenant> tenantOptional = tenantRepository.findById(userId);

    if (tenantOptional.isPresent()) {

      bookingService.cancelAllNonCompletedBookingsForUser(userId);

      Tenant tenantToBeDeleted = tenantOptional.get();

      tenantToBeDeleted.setName("deleted");
      tenantToBeDeleted.setEmail("deleted@gmail.com");
      tenantToBeDeleted.setMobileNumber("00000000");
      tenantToBeDeleted.setUsername("deleted");
      tenantToBeDeleted.setPassword("Deleted123");
      tenantToBeDeleted.setProfilePictureFileName("deleted");
      tenantToBeDeleted.setHouseAddress("deleted");

      tenantRepository.save(tenantToBeDeleted);

    } else {
      throw new IllegalArgumentException("Tenant with ID " + userId + " not found");
    }
  }

  public Tenant get(Long id) {
    return tenantRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Tenant with id " + id + " does not exist."));
  }

  @PostConstruct
  public void init() {
    restrictedUsernamesSet = initializeRestrictedUsernames();
  }

  public void setRestrictedUsernamesSet(Set<String> restrictedUsernames) {
    this.restrictedUsernamesSet = restrictedUsernames;
  }

  public List<Booking> getOwnBookings() {
    User user = getAuthenticatedUser();
    return bookingService.getBookingsByUserID(user.getId());
  }

  public void setBookingStatus(long bookingId, BookingStatus status) {
    User user = getAuthenticatedUser();
    bookingService.setBookingStatus(bookingId, status, user.getId(), currentUserIsAdmin());
  }
}