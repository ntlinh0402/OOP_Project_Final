package com.phonerecommend.controller;

import com.phonerecommend.model.Phone;
import com.phonerecommend.repository.PhoneRepository;
import com.phonerecommend.repository.RepositoryFactory;
import com.phonerecommend.service.filter.PhoneFilter;
import com.phonerecommend.service.filter.impl.CompositeFilter;
import com.phonerecommend.service.search.SearchService;
import com.phonerecommend.service.search.SortingService;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller xử lý tìm kiếm và lọc điện thoại
 */
public class PhoneSearchController {
    private final PhoneRepository phoneRepository;
    private final SearchService searchService;
    private final SortingService sortingService;
    private final CompositeFilter compositeFilter;

    private List<Phone> currentResults;
    private String currentKeyword;

    /**
     * Constructor mặc định
     */
    public PhoneSearchController() {
        this.phoneRepository = RepositoryFactory.getPhoneRepository();
        this.searchService = new SearchService();
        this.sortingService = new SortingService();
        this.compositeFilter = new CompositeFilter(true); // AND filter mặc định
        this.currentResults = new ArrayList<>();
        this.currentKeyword = "";
    }

    /**
     * Tìm kiếm điện thoại theo từ khóa
     * @param keyword Từ khóa tìm kiếm
     * @return Danh sách điện thoại thỏa mãn
     */
    public List<Phone> searchPhones(String keyword) {
        // Lưu lại từ khóa hiện tại
        this.currentKeyword = keyword;

        // Lấy tất cả điện thoại
        List<Phone> allPhones = phoneRepository.getAllPhones();

        // Tìm kiếm theo từ khóa
        List<Phone> searchResults = searchService.searchByKeyword(allPhones, keyword);

        // Áp dụng bộ lọc hiện tại
        currentResults = compositeFilter.filter(searchResults);

        return new ArrayList<>(currentResults);
    }

    /**
     * Lọc điện thoại với bộ lọc chỉ định
     * @param filter Bộ lọc điện thoại
     * @return Danh sách điện thoại thỏa mãn
     */
    public List<Phone> filterPhones(PhoneFilter filter) {
        // Nếu là CompositeFilter, thay thế bộ lọc hiện tại
        if (filter instanceof CompositeFilter) {
            this.compositeFilter.clearFilters();
            for (PhoneFilter f : ((CompositeFilter) filter).getFilters()) {
                this.compositeFilter.addFilter(f);
            }
        } else {
            // Thêm bộ lọc vào bộ lọc tổng hợp
            this.compositeFilter.addFilter(filter);
        }

        // Áp dụng bộ lọc mới lên kết quả hiện tại
        List<Phone> allPhones = phoneRepository.getAllPhones();
        List<Phone> searchResults = searchService.searchByKeyword(allPhones, currentKeyword);
        currentResults = compositeFilter.filter(searchResults);

        return new ArrayList<>(currentResults);
    }

    /**
     * Xóa tất cả bộ lọc
     * @return Danh sách điện thoại không lọc
     */
    public List<Phone> clearFilters() {
        this.compositeFilter.clearFilters();
        List<Phone> allPhones = phoneRepository.getAllPhones();
        currentResults = searchService.searchByKeyword(allPhones, currentKeyword);
        return new ArrayList<>(currentResults);
    }

    /**
     * Thêm bộ lọc vào danh sách bộ lọc hiện tại
     * @param filter Bộ lọc cần thêm
     * @return Danh sách điện thoại sau khi lọc
     */
    public List<Phone> addFilter(PhoneFilter filter) {
        compositeFilter.addFilter(filter);
        return filterPhones(compositeFilter);
    }

    /**
     * Xóa bộ lọc khỏi danh sách bộ lọc hiện tại
     * @param filterId ID của bộ lọc cần xóa
     * @return Danh sách điện thoại sau khi lọc
     */
    public List<Phone> removeFilter(String filterId) {
        List<PhoneFilter> filters = compositeFilter.getFilters();
        for (PhoneFilter filter : new ArrayList<>(filters)) {
            if (filter.getFilterId().equals(filterId)) {
                compositeFilter.removeFilter(filter);
            }
        }

        // Áp dụng lại bộ lọc
        List<Phone> allPhones = phoneRepository.getAllPhones();
        List<Phone> searchResults = searchService.searchByKeyword(allPhones, currentKeyword);
        currentResults = compositeFilter.filter(searchResults);

        return new ArrayList<>(currentResults);
    }

    /**
     * Lấy danh sách bộ lọc hiện tại
     * @return Danh sách bộ lọc
     */
    public List<PhoneFilter> getCurrentFilters() {
        return compositeFilter.getFilters();
    }

    /**
     * Sắp xếp kết quả hiện tại theo tiêu chí
     * @param criteria Tiêu chí sắp xếp
     * @return Danh sách điện thoại đã sắp xếp
     */
    public List<Phone> sortResults(SortingService.SortCriteria criteria) {
        currentResults = sortingService.sort(currentResults, criteria);
        return new ArrayList<>(currentResults);
    }

    /**
     * Lấy kết quả hiện tại
     * @return Danh sách điện thoại hiện tại
     */
    public List<Phone> getCurrentResults() {
        return new ArrayList<>(currentResults);
    }

    /**
     * Lấy tất cả điện thoại
     * @return Danh sách tất cả điện thoại
     */
    public List<Phone> getAllPhones() {
        return phoneRepository.getAllPhones();
    }

    /**
     * Tìm điện thoại theo link
     * @param link Link của điện thoại
     * @return Điện thoại nếu tìm thấy, null nếu không
     */
    public Phone getPhoneByLink(String link) {
        return phoneRepository.findPhoneByLink(link).orElse(null);
    }

    /**
     * Cập nhật lượt xem của điện thoại
     * @param phone Điện thoại cần cập nhật
     */
    public void incrementPhoneViewCount(Phone phone) {
        phone.incrementViewCount();
        phoneRepository.updateViewCount(phone);
    }
}