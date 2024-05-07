<script>
    export let item
    export let media = ''
    import { onMount } from 'svelte'
    let picture
  
    async function load() {
        const data = await fetch(
            `https://api.themoviedb.org/3/movie/${item.movie_id}?api_key=799c0bd0b2baaecc6d9301fadfaea7f7&language=en-US`
        ).then(res => res.json());
      picture = data.poster_path
    }
    
    onMount(() => load())

    function getYear(dateString) {
    const releaseDate = new Date(dateString);
    return releaseDate.getFullYear();
    }
</script>

<a href={`/movie/${item.movie_id}`} class="flex-shrink-0 w-48 mx-2"> <!-- 设置项的固定宽度，添加外边距 -->
    <div class="flex flex-col items-center mb-4 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-800 hover:shadow-lg group">
        <h5 class="text-center text-grey-300 mb-1">{getYear(item.release_date)}</h5>
        <img 
        src={`https://image.tmdb.org/t/p/w500${picture}`} 
        alt={item.title || 'NO PICTURE FOUND'}
        class="rounded-lg mb-2 object-cover group-hover:scale-105 transition-all duration-300"
        >
        <h4 class="text-center font-semibold group-hover:text-blue-500/80">{item.original_title || item.title}</h4>
    </div>
</a>
  