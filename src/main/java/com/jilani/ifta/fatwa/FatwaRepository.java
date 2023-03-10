package com.jilani.ifta.fatwa;

import com.jilani.ifta.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FatwaRepository extends JpaRepository<Fatwa, Long> {

    Fatwa getByFatwaId(String fatwaId);
    Fatwa getByTitle(String title);

    //by tag
    @Query("SELECT f FROM Fatwa as f join f.tags as t " +
            "where t.tagName = :tagName " +
            "and f.isApproved = true " +
            "order by f.answeredOn desc")
    List<Fatwa> getAllByTag(String tagName);

    @Query("SELECT f FROM Fatwa as f join f.tags as t " +
            "where t.tagName = :tagName " +
            "and f.isApproved = true " +
            "order by f.answeredOn desc")
    List<Fatwa> getAllByTag(String tagName, Pageable pageable);

    default List<Fatwa> findByTagPaged(String tagName, int page, int size) {
        return getAllByTag(tagName, PageRequest.of(page, size));
    }

    default List<Fatwa> findTop30ByTag(String tagName) {
        return getAllByTag(tagName, PageRequest.of(0, 30));
    }

    default List<Fatwa> findTop30Latest() {
        return findTop30ByIsApprovedOrderByAnsweredOnDesc(true);
    }

    //by topic
    List<Fatwa> findAllByTopicOrderByAnsweredOn(Topic topic);

    default List<Fatwa> fatawaByTopic(Topic topic) {
        return findAllByTopicOrderByAnsweredOn(topic);
    }

    List<Fatwa> findAllByTitleContains(String title);

    List<Fatwa> searchAllByTitleContainsOrQuestionContainsOrAnswerContains(String title, String question, String answer);

    @Query("select f from Fatwa f " +
            "where " +
            "f.isApproved = true and" +
            "( " +
            "lower(f.title) like %?1% " +
            "or " +
            "lower(f.question) like %?1% " +
            "or " +
            "lower(f.answer) like %?1%" +
            ")")
    List<Fatwa> searchForQuery(@Param("query") String query);

    default List<Fatwa> search(String query) {
        String lowerCaseQuery = query.toLowerCase();
        return searchForQuery(lowerCaseQuery);
    }

    //latest
    List<Fatwa> findAllByOrderByAnsweredOnDesc();

    List<Fatwa> findTop30ByIsApprovedOrderByAnsweredOnDesc(boolean isApproved);


    //------------For Muftis-----------------

    //un-answered older first
    @Query("SELECT f FROM Fatwa as f " +
            "where f.isAnswered = false " +
            "order by f.askedOn asc")
    List<Fatwa> getAllUnanswered();

    @Query("SELECT f FROM Fatwa as f " +
            "where f.isAnswered = false " +
            "and (f.mufti = :mufti or f.mufti is null ) " +
            "order by f.askedOn asc")
    List<Fatwa> getAllUnansweredByMufti(User mufti);

    @Query("SELECT f FROM Fatwa as f " +
            "where f.isAnswered = true " +
            "and f.isApproved = false " +
            "order by f.askedOn asc")
    List<Fatwa> getAllAnswered();

    @Query("SELECT f FROM Fatwa as f " +
            "where f.isAnswered = true " +
            "and f.isApproved = false " +
            "and f.mufti = :mufti " +
            "order by f.askedOn asc")
    List<Fatwa> getAllAnsweredByMufti(User mufti);

    //answered but not approved yet
    /*List<Fatwa> findAllByApprovedFalseOrderByAnsweredOnDesc();


    default List<Fatwa> yetToAnswer() {
        return findAllByAnsweredFalseOrderByAskedOnDesc();
    }

    default List<Fatwa> yetToApprove() {
        return findAllByApprovedFalseOrderByAnsweredOnDesc();
    }
*/

}
