import { ref, computed } from 'vue';
import { useUserStore } from '@/store/UserStore';
import { useRouter } from 'vue-router';

export function useAdminAuth() {
    const userStore = useUserStore();
    const router = useRouter();
    const isAdmin = ref(false);
    const isHotelAdmin = ref(false);
    const isSuperAdmin = ref(false);

    const checkAdminStatus = () => {
        const user = userStore.user;
        if (!user || !user.roles) {
            isAdmin.value = false;
            isHotelAdmin.value = false;
            isSuperAdmin.value = false;
            return false;
        }

        const roles = user.roles;
        
        isSuperAdmin.value = roles.includes('SUPER_ADMIN');
        isHotelAdmin.value = roles.includes('ADMIN_HOTELS');
        isAdmin.value = isSuperAdmin.value || isHotelAdmin.value || 
                       roles.includes('ADMIN_FLIGHTS') || 
                       roles.includes('ADMIN_TOURS') || 
                       roles.includes('ADMIN_BUSES') ||
                       roles.includes('HOTEL_SUPPLIER') ||
                       roles.includes('FLIGHT_SUPPLIER') ||
                       roles.includes('TOUR_SUPPLIER') ||
                       roles.includes('BUS_SUPPLIER');

        return isAdmin.value;
    };

    const hasHotelAdminAccess = () => {
        const user = userStore.user;
        console.log('hasHotelAdminAccess - user:', user);
        console.log('hasHotelAdminAccess - user.roles:', user?.roles);
        
        if (!user || !user.roles) {
            console.log('hasHotelAdminAccess - no user or roles');
            return false;
        }

        const roles = user.roles;
        console.log('hasHotelAdminAccess - roles:', roles);
        
        const hasAccess = roles.includes('SUPER_ADMIN') || 
               roles.includes('ADMIN_HOTELS') || 
               roles.includes('HOTEL_SUPPLIER');
        
        console.log('hasHotelAdminAccess - hasAccess:', hasAccess);
        return hasAccess;
    };

    const hasFlightAdminAccess = () => {
        const user = userStore.user;
        if (!user || !user.roles) {
            return false;
        }

        const roles = user.roles;
        return roles.includes('SUPER_ADMIN') || 
               roles.includes('ADMIN_FLIGHTS') || 
               roles.includes('FLIGHT_SUPPLIER');
    };

    const hasTourAdminAccess = () => {
        const user = userStore.user;
        if (!user || !user.roles) {
            return false;
        }

        const roles = user.roles; 
        return roles.includes('SUPER_ADMIN') || 
               roles.includes('ADMIN_TOURS') || 
               roles.includes('TOUR_SUPPLIER');
    };

    const hasBusAdminAccess = () => {
        const user = userStore.user;
        if (!user || !user.roles) {
            return false;
        }

        const roles = user.roles;
        return roles.includes('SUPER_ADMIN') || 
               roles.includes('ADMIN_BUSES') || 
               roles.includes('BUS_SUPPLIER');
    };

    const requireAdmin = (adminType = 'hotel') => {
        if (!userStore.isLoggedIn) {
            router.push('/login');
            return false;
        }

        let hasAccess = false;
        switch (adminType) {
            case 'hotel':
                hasAccess = hasHotelAdminAccess();
                break;
            case 'flight':
                hasAccess = hasFlightAdminAccess();
                break;
            case 'tour':
                hasAccess = hasTourAdminAccess();
                break;
            case 'bus':
                hasAccess = hasBusAdminAccess();
                break;
            default:
                hasAccess = checkAdminStatus();
        }

        if (!hasAccess) {
            router.push('/unauthorized');
            return false;
        }

        return true;
    };

    const updateAdminStatus = () => {
        checkAdminStatus();
    };

    return {
        isAdmin: computed(() => isAdmin.value),
        isHotelAdmin: computed(() => isHotelAdmin.value),
        isSuperAdmin: computed(() => isSuperAdmin.value),
        checkAdminStatus,
        hasHotelAdminAccess,
        hasFlightAdminAccess,
        hasTourAdminAccess,
        hasBusAdminAccess,
        requireAdmin,
        updateAdminStatus
    };
} 