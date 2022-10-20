package models.board;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import models.entity.BoardData;

@Component
public class BoardDataDao {
	
	@Autowired
	private EntityManager em;
	
	/**
	 * 게시글 추가 / 수정
	 * @param dto
	 * @return
	 */
	public BoardDataDto register(BoardDataDto dto) {
		
		BoardData entity = null;
		
		if(dto.getId() == null) { // 추가
			entity = BoardDataDto.toEntity(dto);
		} else { // 수정
			entity = em.find(BoardData.class, dto.getId());
			entity.setSubject(dto.getSubject());
			entity.setContents(dto.getContents());
			entity.setPoster(dto.getPoster());
		}
		
		em.persist(entity);
		em.flush();
		
		return get(entity.getId());
	}
	
	/**
	 * 게시글 단일 조회
	 * @param id
	 * @return
	 */
	public BoardDataDto get(Long id) {
		
		BoardData entity = em.find(BoardData.class, id);
		
		return entity == null ? null : BoardDataDto.toDto(entity);
	}
	
	/**
	 * 게시글 리스트 조회(페이지 네이션)
	 * @param boardId
	 * @return
	 */
	public List<BoardDataDto> gets(String boardId, int page, int limit) {
		int offset = (page - 1) * limit;
		String sql = "SELECT b FROM BoardData b WHERE boardId = :boardId";
		TypedQuery<BoardData> entities = em.createQuery(sql, BoardData.class);
		entities.setParameter("boardId", boardId);
		entities.setFirstResult(offset);
		entities.setMaxResults(limit);
		
		List<BoardDataDto> boards = entities.getResultStream().map(BoardDataDto::toDto).toList();
		
		return boards;
	}
	
	/**
	 * 게시글 삭제
	 * @param id
	 */
	public void delete(Long id) {
		
		BoardData entity = em.find(BoardData.class, id);
		
		em.remove(entity);
		em.flush();
		
	}

}